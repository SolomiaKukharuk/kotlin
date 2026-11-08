package com.example.workmanagernews

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class NewsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val token = inputData.getString("user_token")

        delay(3000)

        val out = Data.Builder()
            .putString("result", "Успіх!")
            .build()

        return Result.success(out)
    }
}