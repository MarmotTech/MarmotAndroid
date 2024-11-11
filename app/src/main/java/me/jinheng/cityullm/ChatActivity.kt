package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.jinheng.cityullm.screens.ChatScreen
import me.jinheng.cityullm.utils.ModelOperations

class ChatActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelName = intent.getStringExtra("modelName")!!
        val modelInfo = ModelOperations.modelName2modelInfo[modelName]

        enableEdgeToEdge()

        setContent {
            ChatScreen(
                modelInfo = modelInfo!!
            )
        }
    }
}
