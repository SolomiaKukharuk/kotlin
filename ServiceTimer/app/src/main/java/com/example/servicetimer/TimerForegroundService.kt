package com.example.servicetimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    private var timerJob: Job? = null

    private val _timerSeconds = MutableStateFlow(0)

    fun getTimerFlow(): StateFlow<Int> =
        _timerSeconds.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): TimerForegroundService =
            this@TimerForegroundService
    }

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setContentTitle("Таймер працює")
                .setContentText("Відлік часу запущено")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build()

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        if (timerJob == null) {

            timerJob = scope.launch {

                while (true) {

                    delay(1000)

                    _timerSeconds.value++

                    Log.d(
                        "Timer",
                        "Минуло ${_timerSeconds.value} секунд"
                    )
                }
            }
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service",
                    NotificationManager.IMPORTANCE_LOW
                )

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder {
        return binder
    }

    fun stopTimer() {

        timerJob?.cancel()
        timerJob = null

        _timerSeconds.value = 0

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {

        timerJob?.cancel()
        scope.cancel()

        super.onDestroy()
    }
}