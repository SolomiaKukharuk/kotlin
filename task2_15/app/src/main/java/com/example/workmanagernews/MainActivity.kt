package com.example.workmanagernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.workmanagernews.ui.theme.WorkManagerNewsTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val workStatus =
        MutableStateFlow("Задача ще не запускалась")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorkManagerNewsTheme {

                val status by workStatus.collectAsState()

                NewsScreen(
                    status = status,
                    onSyncNow = {
                        startOneTimeSync()
                    },
                    onEnableNightSync = {
                        startPeriodicSync()
                    }
                )
            }
        }
    }

    private fun startOneTimeSync() {

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )
                .build()

        val inputData =
            Data.Builder()
                .putString(
                    "user_token",
                    "abc123"
                )
                .build()

        val request =
            OneTimeWorkRequestBuilder<NewsSyncWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

        val workManager =
            WorkManager.getInstance(this)

        workManager.enqueue(request)

        workManager
            .getWorkInfoByIdLiveData(request.id)
            .observe(this) { workInfo ->

                if (workInfo != null) {

                    when (workInfo.state) {

                        WorkInfo.State.ENQUEUED -> {
                            workStatus.value =
                                "Очікує..."
                        }

                        WorkInfo.State.RUNNING -> {
                            workStatus.value =
                                "Виконується..."
                        }

                        WorkInfo.State.SUCCEEDED -> {

                            val result =
                                workInfo.outputData
                                    .getString("result")

                            workStatus.value =
                                "Успіх: $result"
                        }

                        WorkInfo.State.FAILED -> {
                            workStatus.value =
                                "Помилка"
                        }

                        WorkInfo.State.CANCELLED -> {
                            workStatus.value =
                                "Скасовано"
                        }

                        WorkInfo.State.BLOCKED -> {
                            workStatus.value =
                                "Заблоковано"
                        }
                    }
                }
            }
    }

    private fun startPeriodicSync() {

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.UNMETERED
                )
                .setRequiresCharging(true)
                .build()

        val inputData =
            Data.Builder()
                .putString(
                    "user_token",
                    "abc123"
                )
                .build()

        val request =
            PeriodicWorkRequestBuilder<NewsSyncWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "night_news_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}

@Composable
fun NewsScreen(
    status: String,
    onSyncNow: () -> Unit,
    onEnableNightSync: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = "Синхронізація новин",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Text(
            text = status,
            modifier =
                Modifier.padding(top = 24.dp)
        )

        Button(
            onClick = onSyncNow,
            modifier =
                Modifier.padding(top = 24.dp)
        ) {

            Text("Синхронізувати зараз")
        }

        Button(
            onClick = onEnableNightSync,
            modifier =
                Modifier.padding(top = 12.dp)
        ) {

            Text("Увімкнути нічну синхронізацію")
        }
    }
}