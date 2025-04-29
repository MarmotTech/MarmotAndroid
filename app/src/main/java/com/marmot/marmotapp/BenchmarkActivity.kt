package com.marmot.marmotapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.screens.BenchmarkScreen
import javax.inject.Inject

@AndroidEntryPoint
class BenchmarkActivity: ComponentActivity() {
    @Inject
    lateinit var modelManager: ModelManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            BenchmarkScreen(
                modelManager = modelManager
            )
        }
    }
}
