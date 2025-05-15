package com.marmot.marmotapp.models

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.File
import java.io.FileOutputStream
import com.google.common.io.Files
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject constructor() : ViewModel() {
    private val _data = MutableStateFlow<List<DataInfo>>(emptyList())
    val data: StateFlow<List<DataInfo>> get() = _data

    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())

    suspend fun downloadData(data: List<DataInfo>) {
        withContext(Dispatchers.IO) {
            val downloadJobs = data.map { data ->
                async {
                    downloadDataToFile(data)
                }
            }
            downloadJobs.awaitAll()
        }
    }

    private suspend fun downloadDataToFile(data: DataInfo) {
        withContext(Dispatchers.IO) {
            val url = URL(data.dataUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5 * 1000  // Set connection timeout (5 seconds)
            val totalSize = conn.contentLengthLong
            var downloadedSize: Long = 0

            val tempFile = File.createTempFile("download_${data.dataName}_", ".tmp")
            try {
                val file = File(Config.dataPath + data.dataLocalPath)

                if (file.exists() && file.length() == totalSize.toLong()) {
                    Log.d("MRM", "${data.dataName} already downloaded")
                    return@withContext
                }

                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    conn.disconnect()
                    return@withContext
                }

                val inputStream = conn.inputStream
                val outputStream = FileOutputStream(tempFile)

                var bytesRead: Int
                val buffer = ByteArray(4096)

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                }

                outputStream.close()
                inputStream.close()

                if (tempFile.exists()) {
                    val targetFile = File(Config.dataPath + data.dataLocalPath)
                    if (targetFile.exists()) {
                        targetFile.delete()  // Delete existing target file if any
                    }
                    Files.move(tempFile, targetFile)
                    Log.d("MRM", "Download ${data.dataName} completed and moved to target: ${targetFile.path}")
                }
            } catch (e: Exception) {
                Log.e("MRM", "Error downloading data: ${data.dataName}")
                Log.e("MRM", e.stackTraceToString())
            } finally {
                conn.disconnect()

                if (tempFile.exists()) {
                    tempFile.delete()
                }

                cleanupDownloadTask(data.dataName)
            }
        }
    }

    private fun cleanupDownloadTask(taskID: String) {
        val updatedTasks = _downloadTasks.value.toMutableMap()
        updatedTasks.remove(taskID)
        _downloadTasks.value = updatedTasks
    }
}