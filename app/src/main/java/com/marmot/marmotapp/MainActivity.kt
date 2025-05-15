package com.marmot.marmotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.marmot.marmotapp.models.Llama
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.screens.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        Llama.initFolder(getExternalFilesDir(null))

        setContent {
            LaunchedEffect(Unit) {
                modelManager.fetchModels()
            }

            WelcomeScreen()
        }
    }
}
