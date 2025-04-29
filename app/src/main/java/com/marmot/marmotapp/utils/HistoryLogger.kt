package com.marmot.marmotapp.utils

import com.google.gson.Gson
import com.marmot.marmotapp.models.HistoryItem
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class HistoryLogger(private val historyPath: String, private val maxItems: Int) {
    private var writer: BufferedWriter? = null

    init {
        try {
            writer = BufferedWriter(FileWriter(historyPath, true))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun append2File(item: HistoryItem?) {
        val json = Gson().toJson(item)
        try {
            writer!!.write(json)
            writer!!.newLine()
            writer!!.flush()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class)
    fun readItems(): FixedSizeQueue<HistoryItem> {
        val historyReader = FileReader(historyPath)

        val bufferedReader = BufferedReader(historyReader)
        val queue = FixedSizeQueue<HistoryItem>(maxItems)

        var line: String?
        while ((bufferedReader.readLine().also { line = it }) != null) {
            try {
                val item = Gson().fromJson(
                    line,
                    HistoryItem::class.java
                )
                queue.add(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return queue
    }
}
