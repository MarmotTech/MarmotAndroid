package me.jinheng.cityullm.utils

import android.annotation.SuppressLint

class NativeMessageReceiver {
    private val lock = Any()

    private var receivedString: String? = null

    var isStart: Boolean = false
        private set

    fun receiveStringFromNative(value: String?) {
        synchronized(lock) {
            receivedString = value
            (lock as Object).notifyAll()
        }
    }

    @SuppressLint("DefaultLocale")
    fun receiveStartFromNative(tokenPerSecondPrefill: Float, tokenPerSecondDecode: Float) {
        synchronized(lock) {
            if (tokenPerSecondPrefill == Float.POSITIVE_INFINITY) {
                receivedString = ""
            } else {
                receivedString = String.format(
                    "prefill: %.1f tok/s; decode: %.1f tok/s",
                    tokenPerSecondPrefill,
                    tokenPerSecondDecode
                )
            }
            isStart = true
            (lock as Object).notifyAll()
        }
    }

    fun waitForString(): String? {
        synchronized(lock) {
            while ((receivedString == null) && (!isStart)) {
                try {
                    (lock as Object).wait() // 等待 native 代码发送字符串
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return null
                }
            }
            return receivedString
        }
    }

    fun reset() {
        receivedString = null
        isStart = false
    }
}
