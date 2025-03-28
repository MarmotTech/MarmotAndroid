package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import me.jinheng.cityullm.models.ModelManager
import me.jinheng.cityullm.ui.DownloadBottomSheet
import me.jinheng.cityullm.screens.MenuScreen
import javax.inject.Inject

@AndroidEntryPoint
class MenuActivity: ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            var showBottomSheet by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                if (modelManager.installedModels().isEmpty()) {
                    showBottomSheet = true
                }
            }

            MenuScreen()

            if (showBottomSheet) {
                DownloadBottomSheet(
                    modelManager = modelManager,
                    onComplete = {
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}
