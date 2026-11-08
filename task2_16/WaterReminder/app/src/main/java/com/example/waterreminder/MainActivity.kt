package com.example.waterreminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.waterreminder.ui.theme.WaterReminderTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: WaterReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel =
            ViewModelProvider(this)[WaterReminderViewModel::class.java]

        setContent {

            WaterReminderTheme {

                val reminderCount by
                viewModel.reminderCount.collectAsState()

                WaterReminderScreen(
                    reminderCount = reminderCount,
                    onStartReminder = { interval ->
                        viewModel.startReminder(interval)
                    },
                    onStopReminder = {
                        viewModel.stopReminder()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::viewModel.isInitialized) {
            viewModel.refreshReminderCount()
        }
    }
}

@Composable
fun WaterReminderScreen(
    reminderCount: Int,
    onStartReminder: (Long) -> Unit,
    onStopReminder: () -> Unit
) {

    val context = LocalContext.current

    var reminderEnabled by
    rememberSaveable {
        mutableStateOf(false)
    }

    var intervalText by
    rememberSaveable {
        mutableStateOf("15")
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) {
        }

    LaunchedEffect(Unit) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Трекер води",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = intervalText,
            onValueChange = { newValue ->

                if (newValue.all { it.isDigit() }) {
                    intervalText = newValue
                }
            },
            label = {
                Text("Інтервал у хвилинах")
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Number
                ),
            singleLine = true,
            modifier =
                Modifier.fillMaxWidth()
        )

        Text(
            text = "Мінімальний інтервал — 15 хвилин",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "Увімкнути нагадування"
            )

            Switch(
                checked = reminderEnabled,
                onCheckedChange = { enabled ->

                    reminderEnabled = enabled

                    if (enabled) {

                        val interval =
                            intervalText
                                .toLongOrNull()
                                ?: 15L

                        onStartReminder(interval)

                    } else {

                        onStopReminder()
                    }
                }
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text =
                "Нагадувань відправлено: $reminderCount разів",
            style =
                MaterialTheme.typography.titleMedium
        )
    }
}