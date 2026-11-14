package com.example.privatbankcurrencies

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExchangeRatesSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        val requestedDate =
            inputData.getString("date")
                ?: currentDate()

        if (!isValidDate(requestedDate)) {
            return Result.failure(
                Data.Builder()
                    .putString(
                        "message",
                        "Некоректна дата"
                    )
                    .build()
            )
        }

        val api =
            PrivatBankNetworkModule().service

        val dao =
            ExchangeDatabase
                .getDatabase(applicationContext)
                .exchangeRateDao()

        try {

            val response =
                api.getExchangeRates(
                    requestedDate
                )

            if (!response.isSuccessful) {

                return if (
                    response.code() in 500..599
                ) {
                    Result.retry()
                } else {
                    Result.failure(
                        Data.Builder()
                            .putString(
                                "message",
                                "Помилка сервера: ${response.code()}"
                            )
                            .build()
                    )
                }
            }

            val body =
                response.body()
                    ?: return Result.failure(
                        Data.Builder()
                            .putString(
                                "message",
                                "Порожня відповідь сервера"
                            )
                            .build()
                    )

            if (body.date != requestedDate) {

                return Result.failure(
                    Data.Builder()
                        .putString(
                            "message",
                            "Дата відповіді не відповідає запитаній"
                        )
                        .build()
                )
            }

            val entities =
                body.exchangeRate
                    .filter {
                        !it.currency.isNullOrBlank()
                    }
                    .map { rate ->

                        ExchangeRateEntity(
                            date = body.date,
                            currency =
                                rate.currency!!,
                            baseCurrency =
                                rate.baseCurrency
                                    ?: "UAH",
                            purchaseRate =
                                rate.purchaseRate,
                            saleRate =
                                rate.saleRate
                        )
                    }

            if (entities.isEmpty()) {

                return Result.success(
                    Data.Builder()
                        .putString(
                            "message",
                            "Сервер не повернув курсів"
                        )
                        .build()
                )
            }

            dao.upsertAll(entities)

            return Result.success(
                Data.Builder()
                    .putString(
                        "message",
                        "Курси успішно оновлено"
                    )
                    .build()
            )

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            return Result.retry()
        }
    }

    private fun currentDate(): String {

        return SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(Date())
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