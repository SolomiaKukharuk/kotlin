package com.example.privatbankcurrencies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.privatbankcurrencies.ui.theme.PrivatBankCurrenciesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            PrivatBankCurrenciesTheme {

                ExchangeArchiveScreen()
            }
        }
    }
}

@Composable
fun ExchangeArchiveScreen(
    viewModel: ExchangeArchiveViewModel = viewModel()
) {

    val selectedDate by
    viewModel.selectedDate.collectAsStateWithLifecycle()

    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    val isLoading =
        uiState is ExchangeArchiveUiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "ПриватБанк — валюти",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                viewModel.onDateChanged(it)
            },
            label = {
                Text("Дата")
            },
            placeholder = {
                Text("dd.MM.yyyy")
            },
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                viewModel.loadExchangeRates()
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Показати курси")
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        when (val state = uiState) {

            ExchangeArchiveUiState.Initial -> {

                Text(
                    text =
                        "Введіть дату у форматі dd.MM.yyyy та натисніть «Показати курси»."
                )
            }

            ExchangeArchiveUiState.Loading -> {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator()

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text("Завантаження курсів...")
                }
            }

            is ExchangeArchiveUiState.Error -> {

                Column {

                    Text(
                        text = state.message,
                        color =
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.loadExchangeRates()
                        }
                    ) {

                        Text("Спробувати ще раз")
                    }
                }
            }

            is ExchangeArchiveUiState.Success -> {

                if (state.rates.isEmpty()) {

                    Text(
                        text =
                            "Для цієї дати дані відсутні."
                    )

                } else {

                    Text(
                        text =
                            "Курси на ${state.date}",
                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    LazyColumn(
                        modifier =
                            Modifier.weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        items(state.rates) { rate ->

                            ExchangeRateCard(rate)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeRateCard(
    rate: ExchangeRateItem
) {

    val purchase =
        rate.purchaseRate
            ?.let { "%.2f".format(it) }
            ?: "—"

    val sale =
        rate.saleRate
            ?.let { "%.2f".format(it) }
            ?: "—"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text =
                    rate.currency ?: "Невідома валюта",
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text =
                    "Базова валюта: ${rate.baseCurrency ?: "UAH"}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column {

                    Text("Банк купує")

                    Text(
                        text = purchase,
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text("Банк продає")

                    Text(
                        text = sale,
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}