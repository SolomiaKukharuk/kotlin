package com.example.privatbankcurrencies

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ExchangeRatesApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        schedulePeriodicSync()
    }

    private fun schedulePeriodicSync() {

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )
                .build()

        val periodicRequest =
            PeriodicWorkRequestBuilder<ExchangeRatesSyncWorker>(
                1,
                TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "exchange_rates_periodic",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
    }
}