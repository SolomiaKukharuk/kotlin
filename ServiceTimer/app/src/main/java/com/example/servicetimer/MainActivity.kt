package com.example.servicetimer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.servicetimer.ui.theme.ServiceTimerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val timerFlow = MutableStateFlow(0)

    private var isBound = false

    private var timerService: TimerForegroundService? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            val binder =
                service as TimerForegroundService.LocalBinder

            timerService = binder.getService()
            isBound = true

            lifecycleScope.launch {
                timerService!!
                    .getTimerFlow()
                    .collect { seconds ->
                        timerFlow.value = seconds
                    }
            }
        }

        override fun onServiceDisconnected(
            className: ComponentName
        ) {
            timerService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            ServiceTimerTheme {

                val seconds by
                timerFlow.collectAsStateWithLifecycle()

                TimerScreen(
                    seconds = seconds,
                    onStop = {
                        timerService?.stopTimer()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val intent =
            Intent(
                this,
                TimerForegroundService::class.java
            )

        bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()

        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}

@Composable
fun TimerScreen(
    seconds: Int,
    onStop: () -> Unit
) {

    val context = LocalContext.current

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Foreground + Bound Service",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "$seconds секунд",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(top = 24.dp)
        )

        Button(
            onClick = {

                val intent =
                    Intent(
                        context,
                        TimerForegroundService::class.java
                    )

                ContextCompat.startForegroundService(
                    context,
                    intent
                )
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Старт Таймер")
        }

        Button(
            onClick = onStop,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Стоп Таймер")
        }
    }
}