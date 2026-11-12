package com.example.privatbankcurrencies

data class ExchangeArchiveResponse(
    val date: String,
    val exchangeRate: List<ExchangeRateItem>
)

data class ExchangeRateItem(
    val baseCurrency: String?,
    val currency: String?,
    val purchaseRate: Double?,
    val saleRate: Double?,
    val purchaseRateNB: Double?,
    val saleRateNB: Double?
)