package me.jinheng.cityullm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.jinheng.cityullm.ui.DownloadBottomSheet
import me.jinheng.cityullm.screens.MenuScreen
import me.jinheng.cityullm.utils.ModelUtils


class MenuActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            var showBottomSheet by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                ModelUtils.prepareInitialModel(
                    this@MenuActivity,
                    onShowDownloadDialog = {
                        showBottomSheet = true
                    }
                )
            }

            MenuScreen()

            if (showBottomSheet) {
                DownloadBottomSheet(
                    onComplete = {
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}
