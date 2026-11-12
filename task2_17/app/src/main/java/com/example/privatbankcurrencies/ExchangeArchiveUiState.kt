package com.example.privatbankcurrencies

sealed interface ExchangeArchiveUiState {

    data object Initial : ExchangeArchiveUiState

    data object Loading : ExchangeArchiveUiState

    data class Success(
        val date: String,
        val rates: List<ExchangeRateItem>
    ) : ExchangeArchiveUiState

    data class Error(
        val message: String
    ) : ExchangeArchiveUiState
}