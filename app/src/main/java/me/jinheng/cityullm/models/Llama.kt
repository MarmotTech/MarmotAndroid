package me.jinheng.cityullm.models

import android.util.Log
import me.jinheng.cityullm.utils.ModelOperations
import me.jinheng.cityullm.utils.NativeMessageReceiver
import me.jinheng.cityullm.utils.getTotalMemory
import java.io.File
import kotlin.math.min

object LLama {
    var id: Long = 0L

    var answering: Boolean = false

    var answerState: AnswerState = AnswerState.NO_MESSAGE_NEED_REPLY

    var msg: NativeMessageReceiver = NativeMessageReceiver()

    var input: String? = null

    var curThread: Thread? = null

    init {
        System.loadLibrary("llama-jni")
    }

    fun walkFolder(folderPath: String) {
        val folder = File(folderPath)
        val files = folder.listFiles()
        for (file in files!!) {
            if (file.isDirectory) {
                Log.d("debug", "Directory: " + file.absolutePath)
                walkFolder(file.absolutePath)
            } else if (file.isFile) {
                Log.d("debug", "File: " + file.absolutePath + ", size " + file.length())
            }
        }
    }

    fun initFolder(externalDir: File?) {
        val llamaFolder = File(externalDir, "llama")
        Config.basePath = llamaFolder.absolutePath + "/"

        val cppFolder = File(llamaFolder, "main")
        Config.cppPath = cppFolder.absolutePath + "/"
        if (!cppFolder.exists()) {
            cppFolder.mkdirs()
        }

        val modelFolder = File(llamaFolder, "models")
        Config.modelPath = modelFolder.absolutePath + "/"
        if (!modelFolder.exists()) {
            modelFolder.mkdirs()
        }

        val historyFolder = File(llamaFolder, "history")
        println("modelPath: ${modelFolder.absolutePath}")
        Config.historyPath = historyFolder.absolutePath + "/"
        if (!historyFolder.exists()) {
            historyFolder.mkdirs()
        }

        val dataFolder = File(llamaFolder, "data")
        Config.dataPath = dataFolder.absolutePath + "/"
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }
    }

    fun hasNoInitialModel(): Boolean {
        val modelFolder = File(Config.modelPath)
        if (modelFolder.exists()) {
            val files = modelFolder.listFiles()
            for (f in files!!) {
                if (f.name.endsWith(".gguf")) {
                    Log.d("debug", "Find initial model " + f.absolutePath)
                    return false
                }
            }
        } else {
            Log.d("debug", modelFolder.absolutePath + " does not exist")
        }
        return true
    }

    external fun inputString(s: String?)

    external fun startChat(
        msg: NativeMessageReceiver?,
        localModelPath: String?,
        systemPrompt: String?,
        threadNum: Int
    )

    private external fun startChatWPrefetch(
        msg: NativeMessageReceiver,
        localModelPath: String,
        systemPrompt: String,
        threadNum: Int,
        prefetchSizeInGB: Float,
        lSize: Float
    )

    private external fun startChatWPrefetch(
        msg: NativeMessageReceiver,
        localModelPath: String,
        systemPrompt: String,
        threadNum: Int,
        memSize: Float
    )

    external fun stop()

    external fun kill()

    fun init(modelName: String, enablePrefetch: Boolean, listener: ChatListener) {
        val mInfo = ModelOperations.modelName2modelInfo[modelName]
        val totalMemory = (getTotalMemory() / Constants.GB).toFloat()
        val canUseMemory = min(totalMemory.toDouble(), Config.maxMemorySize.toDouble())
            .toFloat()
        val modelSize = mInfo!!.modelSize.toFloat() / Constants.GB

        var prefetchSizeInGB = 0f
        var kvCacheSizeInGB = 0f
        val memSize = 0f

        if (canUseMemory <= modelSize) {
            prefetchSizeInGB = mInfo.prefetchSize.toFloat() / Constants.GB
            kvCacheSizeInGB = mInfo.kvSize.toFloat() / Constants.GB
        }

        println(
            """
                INIT: $modelName
                path: ${mInfo.modelLocalPath}
                prefetch: $enablePrefetch
                """.trimIndent()
        )
        if (enablePrefetch) {
            startChatWPrefetch(
                msg, mInfo.modelLocalPath,
                mInfo.systemPrompt,
                Config.threadNum,
                memSize
            )
            //            startChatWPrefetch(msg, mInfo.getModelLocalPath(),
//                    mInfo.getSystemPrompt(),
//                    Config.threadNum,
//                    0,0);
        } else {
            startChat(
                msg, mInfo.modelLocalPath,
                mInfo.systemPrompt,
                Config.threadNum
            )
        }
        println("Start thread to receive new strings")
        curThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                msg.reset()
                val s: String = msg.waitForString()!!

                if (answerState == AnswerState.NO_MESSAGE_NEED_REPLY) {
                    //
                } else if (answerState == AnswerState.MESSAGE_NEED_REPLY) {
                    answerState = AnswerState.ANSWERING
                    listener.onBotContinue(s)
                } else {
                    if (msg.isStart) {
                        listener.onUpdateInfo(s)
                        answerState =
                            AnswerState.NO_MESSAGE_NEED_REPLY
                    } else {
                        listener.onBotContinue(s)
                    }
                }
            }
        }
        curThread!!.start()
    }

    @Throws(RuntimeException::class)
    fun run(input_: String?) {
        input = input_
        inputString(input)
        answerState = AnswerState.MESSAGE_NEED_REPLY
    }

    fun destroy() {
        kill()
        curThread!!.interrupt()
    }

    fun clear() {
//        messageAdapter.clear();
    }

    interface ChatListener {
        fun onUpdateInfo(s: String)
        fun onBotContinue(s: String)
    }
}
