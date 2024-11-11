package me.jinheng.cityullm.utils

import android.app.Activity
import android.content.res.AssetManager
import me.jinheng.cityullm.models.Config
import me.jinheng.cityullm.models.LLama
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ModelUtils {
    private fun copyFileFromAssets(assetManager: AssetManager, initialModelName: String, modelDir: String): Boolean {
        println("Copy: $initialModelName")
        return try {
            val inputStream = assetManager.open(initialModelName)
            val outFile = File(modelDir + initialModelName)
            val outputStream = FileOutputStream(outFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun prepareInitialModel(activity: Activity, onShowDownloadDialog: () -> Unit) {
        val assetManager = activity.assets

        try {
            Thread {
                val initialModelName = "ggml-model-tinyllama-1.1b-chat-v1.0-q4_0.gguf"
                val modelInfoName = "models.json"

                val file = assetManager.list("")
                if (file?.contains(initialModelName) == true) {
                    copyFileFromAssets(assetManager, initialModelName, Config.modelPath!!)
                }
                if (file?.contains(modelInfoName) == true) {
                    copyFileFromAssets(assetManager, modelInfoName, Config.modelPath!!)
                }
                ModelOperations.updateModels()
                ModelOperations.allSupportModels

                if (LLama.hasNoInitialModel()) {
                    activity.runOnUiThread {
                        onShowDownloadDialog()
                    }
                }
            }.start()
        } catch (e: IOException) {
            e.printStackTrace();
        }
    }
}
