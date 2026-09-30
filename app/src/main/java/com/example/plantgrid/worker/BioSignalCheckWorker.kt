package com.example.plantgrid.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantgrid.MainActivity
import com.example.plantgrid.data.local.PlantDatabase
import com.example.plantgrid.data.model.PlantMood
import kotlinx.coroutines.flow.first

class BioSignalCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = PlantDatabase.getDatabase(applicationContext)
        val dao = database.sensorNodeDao()
        
        val nodes = dao.getAllNodes().first()
        
        nodes.forEach { node ->
            if (node.mood == PlantMood.DEFENDING || (node.mood == PlantMood.DEHYDRATED && node.moisture < 0.2f)) {
                sendAlertNotification(node.id, node.name, node.mood.displayName)
            }
        }
        
        return Result.success()
    }

    private fun sendAlertNotification(nodeId: String, nodeName: String, status: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "plant_alerts"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Plant Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("node_id", nodeId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Critical Crop Alert: $nodeName")
            .setContentText("Status: $status. Immediate intervention required.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_directions, "Locate on Map", pendingIntent)
            .build()
            
        notificationManager.notify(nodeId.hashCode(), notification)
    }
}
