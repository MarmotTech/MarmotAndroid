package com.marmot.marmotapp

import android.os.Bundle
import android.view.Display.Mode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.screens.SettingsScreen
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity: ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelName = intent.getStringExtra("modelName")!!
        val modelInfo = modelManager.getModelByName(modelName)

        enableEdgeToEdge()

        setContent {
            SettingsScreen(
                modelInfo = modelInfo
            )
        }
    }
}
