package com.example.privatbankcurrencies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeArchiveViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context =
        application.applicationContext

    private val dao =
        ExchangeDatabase
            .getDatabase(context)
            .exchangeRateDao()

    private val workManager =
        WorkManager.getInstance(context)

    private val _selectedDate =
        MutableStateFlow("")

    val selectedDate: StateFlow<String> =
        _selectedDate.asStateFlow()

    val rates: StateFlow<List<ExchangeRateEntity>> =
        _selectedDate
            .flatMapLatest { date ->

                if (date.isBlank()) {
                    flowOf(emptyList())
                } else {
                    dao.observeRates(date)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _syncStatus =
        MutableStateFlow<String?>(null)

    val syncStatus: StateFlow<String?> =
        _syncStatus.asStateFlow()

    fun onDateChanged(
        date: String
    ) {
        _selectedDate.value = date
        _syncStatus.value = null
    }

    fun refreshRates() {

        val date =
            _selectedDate.value.trim()

        if (!isValidDate(date)) {

            _syncStatus.value =
                "Некоректна дата"

            return
        }

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )
                .build()

        val request =
            OneTimeWorkRequestBuilder<ExchangeRatesSyncWorker>()
                .setInputData(
                    workDataOf(
                        "date" to date
                    )
                )
                .setConstraints(constraints)
                .build()

        val workName =
            "exchange_rates_$date"

        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.KEEP,
            request
        )

        observeWork(request.id)
    }

    private fun observeWork(
        workId: java.util.UUID
    ) {

        workManager
            .getWorkInfoByIdLiveData(workId)
            .observeForever { workInfo ->

                if (workInfo == null) {
                    return@observeForever
                }

                _syncStatus.value =
                    when (workInfo.state) {

                        WorkInfo.State.ENQUEUED ->
                            "Очікує мережі / запуску"

                        WorkInfo.State.RUNNING ->
                            "Оновлюється"

                        WorkInfo.State.SUCCEEDED ->
                            workInfo.outputData
                                .getString("message")
                                ?: "Курси оновлено"

                        WorkInfo.State.FAILED ->
                            workInfo.outputData
                                .getString("message")
                                ?: "Помилка оновлення"

                        WorkInfo.State.CANCELLED ->
                            "Оновлення скасовано"

                        WorkInfo.State.BLOCKED ->
                            "Очікує запуску"
                    }
            }
    }

    private fun isValidDate(
        value: String
    ): Boolean {

        val formatter =
            SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            ).apply {
                isLenient = false
            }

        return try {

            formatter.parse(value) != null

        } catch (e: Exception) {

            false
        }
    }
}