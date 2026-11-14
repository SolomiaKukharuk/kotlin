package com.example.privatbankcurrencies

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Upsert
    suspend fun upsertAll(
        rates: List<ExchangeRateEntity>
    )

    @Query(
        "SELECT * FROM exchange_rates " +
                "WHERE date = :date " +
                "ORDER BY currency ASC"
    )
    fun observeRates(
        date: String
    ): Flow<List<ExchangeRateEntity>>
}