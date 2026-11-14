package com.example.privatbankcurrencies

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeArchiveRepository(
    private val api: PrivatBankApi =
        PrivatBankNetworkModule().service
) {

    fun getExchangeRates(
        date: String,
        onSuccess: (ExchangeArchiveResponse) -> Unit,
        onError: (String) -> Unit
    ) {

        api.getExchangeRates(date)
            .enqueue(
                object : Callback<ExchangeArchiveResponse> {

                    override fun onResponse(
                        call: Call<ExchangeArchiveResponse>,
                        response: Response<ExchangeArchiveResponse>
                    ) {

                        if (response.isSuccessful) {

                            val body = response.body()

                            if (body != null) {
                                onSuccess(body)
                            } else {
                                onError("Порожня відповідь сервера")
                            }

                        } else {

                            onError(
                                "Помилка сервера: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<ExchangeArchiveResponse>,
                        t: Throwable
                    ) {

                        onError(
                            t.message
                                ?: "Помилка мережі"
                        )
                    }
                }
            )
    }
}