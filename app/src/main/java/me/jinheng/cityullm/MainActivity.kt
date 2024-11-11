package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.jinheng.cityullm.models.LLama
import me.jinheng.cityullm.screens.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        LLama.initFolder(getExternalFilesDir(null))

        setContent {
            WelcomeScreen()
        }
    }
}
