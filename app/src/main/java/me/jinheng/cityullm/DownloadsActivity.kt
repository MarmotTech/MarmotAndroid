package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import me.jinheng.cityullm.models.ModelManager
import me.jinheng.cityullm.screens.DownloadsScreen
import javax.inject.Inject

@AndroidEntryPoint
class DownloadsActivity: ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DownloadsScreen(
                modelManager = modelManager
            )
        }
    }
}
