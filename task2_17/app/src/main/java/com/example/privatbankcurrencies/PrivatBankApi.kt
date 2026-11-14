package com.example.privatbankcurrencies

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivatBankApi {

    @GET("exchange_rates?json")
    suspend fun getExchangeRates(
        @Query("date") date: String
    ): Response<ExchangeArchiveResponse>
}