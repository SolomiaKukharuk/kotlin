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

                ExchangeRatesScreen()
            }
        }
    }
}

@Composable
fun ExchangeRatesScreen(
    viewModel: ExchangeArchiveViewModel = viewModel()
) {

    val selectedDate by
    viewModel.selectedDate.collectAsStateWithLifecycle()

    val rates by
    viewModel.rates.collectAsStateWithLifecycle()

    val syncStatus by
    viewModel.syncStatus.collectAsStateWithLifecycle()

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {
                viewModel.refreshRates()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Оновити")
        }

        if (syncStatus != null) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = syncStatus!!,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        when {

            selectedDate.isBlank() -> {

                Text(
                    text =
                        "Введіть дату у форматі dd.MM.yyyy"
                )
            }

            rates.isEmpty() -> {

                Text(
                    text =
                        "Курси за цю дату ще не завантажені"
                )
            }

            else -> {

                Text(
                    text = "Курси на $selectedDate",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                if (
                    syncStatus ==
                    "Сервер не повернув курсів"
                ) {

                    Text(
                        text =
                            "Показані раніше збережені дані",
                        style =
                            MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = rates,
                        key = {
                            "${it.date}_${it.currency}"
                        }
                    ) { rate ->

                        ExchangeRateCard(rate)
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeRateCard(
    rate: ExchangeRateEntity
) {

    val purchase =
        rate.purchaseRate
            ?.let {
                "%.2f".format(it)
            }
            ?: "—"

    val sale =
        rate.saleRate
            ?.let {
                "%.2f".format(it)
            }
            ?: "—"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = rate.currency,
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text =
                    "Базова валюта: ${rate.baseCurrency}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
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