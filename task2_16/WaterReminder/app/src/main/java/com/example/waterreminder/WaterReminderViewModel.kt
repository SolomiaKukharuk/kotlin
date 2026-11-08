package com.example.waterreminder

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class WaterReminderViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context =
        application.applicationContext

    private val preferences =
        context.getSharedPreferences(
            "water_preferences",
            Context.MODE_PRIVATE
        )

    private val _reminderCount =
        MutableStateFlow(
            preferences.getInt(
                "reminder_count",
                0
            )
        )

    val reminderCount: StateFlow<Int> =
        _reminderCount

    fun startReminder(
        intervalMinutes: Long
    ) {

        val safeInterval =
            if (intervalMinutes < 15) {
                15L
            } else {
                intervalMinutes
            }

        val request =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(
                safeInterval,
                TimeUnit.MINUTES
            ).build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "water_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }

    fun stopReminder() {

        WorkManager
            .getInstance(context)
            .cancelUniqueWork(
                "water_reminder"
            )
    }

    fun refreshReminderCount() {

        _reminderCount.value =
            preferences.getInt(
                "reminder_count",
                0
            )
    }
}