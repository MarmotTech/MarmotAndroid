package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import me.jinheng.cityullm.models.LLama
import me.jinheng.cityullm.models.ModelManager
import me.jinheng.cityullm.screens.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        LLama.initFolder(getExternalFilesDir(null))

        setContent {
            LaunchedEffect(Unit) {
                modelManager.fetchModels()
            }

            WelcomeScreen()
        }
    }
}
