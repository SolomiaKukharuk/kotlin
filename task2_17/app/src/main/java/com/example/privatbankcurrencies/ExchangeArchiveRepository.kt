package com.example.privatbankcurrencies

class ExchangeArchiveRepository(
    private val api: PrivatBankApi =
        PrivatBankNetworkModule().service
) {

    suspend fun getExchangeRates(
        date: String,
        onSuccess: (ExchangeArchiveResponse) -> Unit,
        onError: (String) -> Unit
    ) {

        try {

            val response = api.getExchangeRates(date)

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

        } catch (t: Throwable) {

            onError(
                t.message
                    ?: "Помилка мережі"
            )
        }
    }
}