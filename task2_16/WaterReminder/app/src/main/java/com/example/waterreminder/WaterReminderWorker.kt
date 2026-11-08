package com.example.waterreminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WaterReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val preferences =
            applicationContext.getSharedPreferences(
                "water_preferences",
                Context.MODE_PRIVATE
            )

        val currentCount =
            preferences.getInt(
                "reminder_count",
                0
            )

        preferences.edit()
            .putInt(
                "reminder_count",
                currentCount + 1
            )
            .apply()

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channelId = "water_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Нагадування про воду",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            manager.createNotificationChannel(channel)
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            )
                .setContentTitle("Трекер води")
                .setContentText(
                    "Час випити склянку води! 💧"
                )
                .setSmallIcon(
                    R.drawable.ic_water_drop
                )
                .build()

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )

        return Result.success()
    }
}