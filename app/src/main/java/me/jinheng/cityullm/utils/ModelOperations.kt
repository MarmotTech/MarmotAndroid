package me.jinheng.cityullm.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.jinheng.cityullm.models.Config
import me.jinheng.cityullm.models.ModelInfo
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import kotlin.concurrent.Volatile

object ModelOperations {
    var modelName2modelInfo: HashMap<String, ModelInfo> = HashMap()

    var modelInfoUrl: String = "https://conference.cs.cityu.edu.hk/saccps/app/models/models.json"

    @Volatile
    private var downloadStatus = DownloadStatus.RUNNING

    @Throws(Exception::class)
    fun downloadFile(fileUrl: String, filePath: String, listener: ProgressListener?): Boolean {
        Log.d("debug", "Download model file from $fileUrl")
        Log.d("debug", "Save model in $filePath")
        val url = URL(fileUrl)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 5 * 1000
        val totalSize = conn.contentLength
        var downloadedSize = 0

        val outFile = File(filePath)
        if (outFile.exists() && outFile.length() == totalSize.toLong()) {
            Log.d("debug", "$filePath has been downloaded")
            return true
        }

        if (conn.responseCode != HttpURLConnection.HTTP_OK) {
            conn.disconnect()
            return false
        }

        val inputStream = conn.inputStream
        val outputStream = FileOutputStream(filePath)

        var bytesRead = 0
        val buffer = ByteArray(4096)
        while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
            outputStream.write(buffer, 0, bytesRead)
            downloadedSize += bytesRead
            val progress = ((downloadedSize * 100L) / totalSize).toInt()
            listener?.onProgressUpdate(progress)

            // Check the download status
            synchronized(downloadStatus) {
                while (downloadStatus == DownloadStatus.PAUSED) {
                    try {
                        (downloadStatus as Object).wait()
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        return false
                    }
                }
                if (downloadStatus == DownloadStatus.CANCELED) {
                    outputStream.close()
                    inputStream.close()
                    conn.disconnect()
                    File(filePath).delete() // Optionally remove the partial file
                    return false
                }
            }
        }
        outputStream.close()
        inputStream.close()
        conn.disconnect()
        Log.d("debug", "$fileUrl is downloaded")

        listener?.onCompleted()

        return true
    }

    fun updateModels() {
        val modelInfoPath = Config.modelPath + "models.json"
        val localFile = File(modelInfoPath)
        if (localFile.exists()) {
            println("Using local models.json")
            try {
                val content = FileUtils.readFileToString(localFile, "utf-8")
                val itemType = object : TypeToken<List<ModelInfo>>() {}.type

                val models = Gson().fromJson<List<ModelInfo>>(
                    content,
                    itemType
                )
                modelName2modelInfo.clear()
                for (info in models) {
//                    if (info.tasks != null) {
//                        benchmarkTasksJson = info.tasks.toJSONString()
//                    }
                    info.modelLocalPath = Config.modelPath + info.modelLocalPath
                    modelName2modelInfo[info.modelName] = info
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Using online models.json")
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                try {
                    // Download metadata of models from server
                    val result = downloadFile(
                        modelInfoUrl,
                        modelInfoPath,
                        null
                    )
                    if (result) {
                        val remoteFile = File(modelInfoPath)
                        val content =
                            FileUtils.readFileToString(remoteFile, "utf-8")
                        val itemType = object : TypeToken<List<ModelInfo>>() {}.type

                        val models = Gson().fromJson<List<ModelInfo>>(
                            content,
                            itemType
                        )

                        for (info in models) {
                            info.modelLocalPath =
                                Config.modelPath + info.modelLocalPath
                            modelName2modelInfo[info.modelName] = info
                        }
                    } else {
                        Log.d(
                            "debug",
                            "$modelInfoUrl cannot be downloaded"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val allSupportModels: List<ModelInfo>
        get() {
            val models: List<ModelInfo> = ArrayList(
                modelName2modelInfo.values
            )
            return models
        }

   fun downloadModelAsync(modelName: String, listener: ProgressListener?) {
        val executorService = Executors.newSingleThreadExecutor()
        val model = getModelInfo(modelName)

        executorService.execute {
            try {
                downloadFile(
                    model!!.modelUrl, model.modelLocalPath, listener
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun pauseDownload() {
        synchronized(downloadStatus) {
            downloadStatus = DownloadStatus.PAUSED
        }
    }

    fun resumeDownload() {
        synchronized(downloadStatus) {
            downloadStatus = DownloadStatus.RUNNING
            (downloadStatus as Object).notifyAll()
        }
    }

    fun cancelDownload() {
        synchronized(downloadStatus) {
            downloadStatus = DownloadStatus.CANCELED
            (downloadStatus as Object).notifyAll()
        }
    }

    fun getModelInfo(modelName: String): ModelInfo? {
        if (modelName2modelInfo.isEmpty()) {
            updateModels()
        }
        if (modelName2modelInfo[modelName] != null) {
            return modelName2modelInfo[modelName]
        } else {
            Log.d("debug", "Cannot find the model $modelName")
            return null
        }
    }

    fun deleteModel(modelName: String): Boolean {
        val modelInfo = checkNotNull(modelName2modelInfo[modelName])
        val f = File(modelInfo.modelLocalPath)
        return f.delete()
    }

    enum class DownloadStatus {
        RUNNING,
        PAUSED,
        CANCELED
    }

    interface ProgressListener {
        fun onProgressUpdate(progress: Int)
        fun onCompleted()
    }
}
