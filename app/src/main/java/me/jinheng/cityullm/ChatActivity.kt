package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import me.jinheng.cityullm.models.ModelManager
import me.jinheng.cityullm.screens.ChatScreen
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
}
