package com.example.privatbankcurrencies

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.util.Log

class ExchangeArchiveViewModel : ViewModel() {

    private val api =
        PrivatBankNetworkModule().service

    private val _selectedDate =
        MutableStateFlow("")

    val selectedDate: StateFlow<String> =
        _selectedDate.asStateFlow()

    private val _uiState =
        MutableStateFlow<ExchangeArchiveUiState>(
            ExchangeArchiveUiState.Initial
        )

    val uiState: StateFlow<ExchangeArchiveUiState> =
        _uiState.asStateFlow()

    private var currentCall:
            Call<ExchangeArchiveResponse>? = null

    fun onDateChanged(date: String) {
        _selectedDate.value = date
    }

    fun loadExchangeRates() {

        val date = _selectedDate.value.trim()

        if (!isValidDate(date)) {
            _uiState.value =
                ExchangeArchiveUiState.Error(
                    "Введіть коректну дату у форматі dd.MM.yyyy"
                )
            return
        }

        currentCall?.cancel()

        _uiState.value =
            ExchangeArchiveUiState.Loading

        Log.d(
            "PRIVAT_API",
            "Початок запиту курсів на дату: $date"
        )

        val call =
            api.getExchangeRates(date)

        currentCall = call

        call.enqueue(
            object : Callback<ExchangeArchiveResponse> {

                override fun onResponse(
                    call: Call<ExchangeArchiveResponse>,
                    response: Response<ExchangeArchiveResponse>
                ) {

                    if (response.isSuccessful) {

                        val body = response.body()

                        if (body != null) {

                            val visibleRates =
                                body.exchangeRate.filter {
                                    !it.currency.isNullOrBlank()
                                }

                            _uiState.value =
                                ExchangeArchiveUiState.Success(
                                    date = body.date,
                                    rates = visibleRates
                                )

                        } else {

                            _uiState.value =
                                ExchangeArchiveUiState.Error(
                                    "Сервер повернув порожню відповідь"
                                )
                        }

                    } else {

                        _uiState.value =
                            ExchangeArchiveUiState.Error(
                                "Помилка сервера: ${response.code()}"
                            )
                    }

                    currentCall = null
                }

                override fun onFailure(
                    call: Call<ExchangeArchiveResponse>,
                    t: Throwable
                ) {

                    if (!call.isCanceled) {

                        _uiState.value =
                            ExchangeArchiveUiState.Error(
                                "Не вдалося завантажити курси. " +
                                        "Перевірте з'єднання та спробуйте знову"
                            )
                    }

                    currentCall = null
                }
            }
        )
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

        val parsedDate: Date =
            try {
                formatter.parse(value)
                    ?: return false
            } catch (e: Exception) {
                return false
            }

        val today =
            Calendar.getInstance()

        if (parsedDate.after(today.time)) {
            return false
        }

        val fourYearsAgo =
            Calendar.getInstance().apply {
                add(Calendar.YEAR, -4)
            }

        if (parsedDate.before(fourYearsAgo.time)) {
            return false
        }

        return true
    }

    override fun onCleared() {

        currentCall?.cancel()

        super.onCleared()
    }
}