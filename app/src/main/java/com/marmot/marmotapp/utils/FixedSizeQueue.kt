package com.marmot.marmotapp.utils

import java.util.LinkedList

class FixedSizeQueue<T>(private val maxSize: Int) {
    private val queue = LinkedList<T>()

    fun add(e: T) {
        if (queue.size >= maxSize) {
            queue.poll()
        }
        queue.offer(e)
    }

    val size: Int
        get() = queue.size

    fun remove(): T? {
        return queue.poll()
    }
}
