package com.marmot.marmotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marmot.marmotapp.models.Llama
import dagger.hilt.android.AndroidEntryPoint
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.screens.ChatScreen
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity: ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelName = intent.getStringExtra("modelName")!!
        val modelInfo = modelManager.getModelByName(modelName)

        enableEdgeToEdge()

        setContent {
            ChatScreen(
                modelInfo = modelInfo
            )
        }
    }

    override fun onDestroy() {
        Llama.destroy()
        super.onDestroy()
    }
}
