package com.example.privatbankcurrencies

import androidx.room3.Entity

@Entity(
    tableName = "exchange_rates",
    primaryKeys = ["date", "currency"]
)
data class ExchangeRateEntity(
    val date: String,
    val currency: String,
    val baseCurrency: String,
    val purchaseRate: Double?,
    val saleRate: Double?
)