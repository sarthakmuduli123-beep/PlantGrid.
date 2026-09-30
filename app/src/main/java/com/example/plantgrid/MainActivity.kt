package com.example.plantgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.plantgrid.ui.navigation.AdaptiveNavDisplay
import com.example.plantgrid.ui.theme.PlantGridTheme
import com.example.plantgrid.worker.BioSignalCheckWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Register Background Alert Worker
        try {
            val workRequest = PeriodicWorkRequestBuilder<BioSignalCheckWorker>(15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(this).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            PlantGridTheme {
                AdaptiveNavDisplay()
            }
        }
    }
}
