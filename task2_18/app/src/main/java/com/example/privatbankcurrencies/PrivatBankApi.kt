package com.example.privatbankcurrencies

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivatBankApi {

    @GET("exchange_rates?json")
    fun getExchangeRates(
        @Query("date") date: String
    ): Call<ExchangeArchiveResponse>
}