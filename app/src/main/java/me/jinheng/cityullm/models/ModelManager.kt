package me.jinheng.cityullm.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.offline.DownloadProgress
import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelManager @Inject constructor() : ViewModel() {
    private val _models = MutableStateFlow<List<ModelInfo>>(emptyList())
    val models: StateFlow<List<ModelInfo>> get() = _models

    private val _downloadProgress = MutableStateFlow(0.0)
    val downloadProgress: StateFlow<Double> get() = _downloadProgress

    private val _downloadTasks = MutableStateFlow<Map<String, DownloadTask>>(emptyMap())

    val isDownloading: Flow<Boolean> = _downloadTasks
        .map { it.isNotEmpty() }

    suspend fun fetchModels() {
        Log.d("MRM", "fetchModels called ${this}")
        withContext(Dispatchers.IO) {
            val url = URL("https://conference.cs.cityu.edu.hk/saccps/app/models/models.json")
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connect()
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                _models.value = Gson().fromJson(response, object : TypeToken<List<ModelInfo>>() {}.type) ?: listOf()
                Log.d("WTF", "${models}")
            } finally {
                connection.disconnect()
            }
        }
    }

    fun modelDownloading(model: ModelInfo): Flow<Boolean> {
        return _downloadTasks.map {
            it.containsKey(model.modelName)
        }
    }

    fun getModelByName(name: String): ModelInfo {
        return models.value.first {
            it.modelName == name
        }
    }

    fun installedModels(): List<ModelInfo> {
        return models.value.filter { model ->
            val modelFile = File(Config.modelPath + model.modelLocalPath)
            modelFile.exists()
        }
    }

    fun missingModels(): List<ModelInfo> {
        Log.d("MRM", "missingModels called ${models.value}")
        return models.value.filter { model ->
            val modelFile = File(Config.modelPath + model.modelLocalPath)
            !modelFile.exists()
        }
    }

    suspend fun removeModels(models: List<ModelInfo>) {
        withContext(Dispatchers.IO) {
            for (model in models) {
                try {
                    val modelFile = File(Config.modelPath + model.modelLocalPath)
                    if (modelFile.exists()) {
                        modelFile.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun downloadModels(models: List<ModelInfo>) {
        withContext(Dispatchers.IO) {
            val downloadJobs = models.map { model ->
                async {
                    downloadModelToFile(model)
                }
            }

            downloadJobs.awaitAll()
        }
    }

    private suspend fun downloadModelToFile(model: ModelInfo) {
        val updatedTasks = _downloadTasks.value.toMutableMap()
        updatedTasks[model.modelName] = DownloadTask(model, 0.0)
        _downloadTasks.value = updatedTasks

        withContext(Dispatchers.IO) {
            val url = URL(model.modelUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5 * 1000  // Set connection timeout (5 seconds)
            val totalSize = conn.contentLengthLong
            var downloadedSize: Long = 0

            val tempFile = File.createTempFile("download_${model.modelName}_", ".tmp")
            try {
                val file = File(Config.modelPath + model.modelLocalPath)

                if (file.exists() && file.length() == totalSize.toLong()) {
                    Log.d("MRM", "${model.modelName} already downloaded")
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

                // Start download and update progress
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead

                    // Calculate progress
                    val progress = ((downloadedSize.toDouble() * 100) / totalSize.toDouble())

                    // Update progress in the listener
                    updateDownloadProgress(model.modelName, progress)
                }

                outputStream.close()
                inputStream.close()

                // Once download is complete, move the temp file to the target location
                if (tempFile.exists()) {
                    val targetFile = File(Config.modelPath + model.modelLocalPath)
                    if (targetFile.exists()) {
                        targetFile.delete()  // Delete existing target file if any
                    }
                    Files.move(tempFile, targetFile)
                    Log.d("MRM", "Download ${model.modelName} completed and moved to target: ${targetFile.path}")
                }

            } catch (e: Exception) {
                Log.e("MRM", "Error downloading model: ${model.modelName}")
                Log.e("MRM", e.stackTraceToString())
            } finally {
                conn.disconnect()
                // Clean up the temporary file if it exists
                if (tempFile.exists()) {
                    tempFile.delete()
                }

                // Update the download status
                cleanupDownloadTask(model.modelName)
            }
        }
    }

    private fun updateDownloadProgress(modelName: String, progress: Double) {
        val updatedTasks = _downloadTasks.value.toMutableMap()
        val updatedTask = updatedTasks[modelName]?.copy(downloadProgress = progress)
        updatedTask?.let {
            updatedTasks[modelName] = it
        }
        _downloadTasks.value = updatedTasks

        val totalProgress = updatedTasks.values.sumOf { it.downloadProgress } /
                updatedTasks.size.toDouble()

        _downloadProgress.value = totalProgress
    }

    private fun cleanupDownloadTask(taskID: String) {
        val updatedTasks = _downloadTasks.value.toMutableMap()
        updatedTasks.remove(taskID)
        _downloadTasks.value = updatedTasks
    }
}

data class DownloadTask(
    val model: ModelInfo,
    val downloadProgress: Double
)

@Module
@InstallIn(SingletonComponent::class)
object ModelManagerModule {
    @Provides
    @Singleton
    fun provideModelManager(
    ): ModelManager {
        return ModelManager()
    }
}
