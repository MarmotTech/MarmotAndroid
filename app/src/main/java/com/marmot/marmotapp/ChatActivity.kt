package com.marmot.marmotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marmot.marmotapp.models.ChatHistory
import com.marmot.marmotapp.models.LLama
import com.marmot.marmotapp.models.ModelInfo
import com.marmot.marmotapp.models.ModelManager
import dagger.hilt.android.AndroidEntryPoint
import com.marmot.marmotapp.screens.ChatScreen
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity: ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatId = intent.getStringExtra("chatId")
        val intentModelName = intent.getStringExtra("modelName")
        var modelInfo: ModelInfo? = null

        if (chatId != null) {
            val chatHistory = ChatHistory.getById(this, chatId)!!
            val modelName = chatHistory.modelName
            modelInfo = modelManager.getModelByName(modelName)
        }
        if (modelInfo == null && intentModelName != null) {
            modelInfo = modelManager.getModelByName(intentModelName)
        }

        enableEdgeToEdge()

        setContent {
            ChatScreen(
                modelInfo = modelInfo!!,
                chatHistoryId = chatId
            )
        }
    }

    override fun onDestroy() {
        LLama.destroy()
        super.onDestroy()
    }
}
