package com.example.broadcastjournal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BroadcastRepository(
    private val context: Context
) {

    fun observeBroadcasts(): Flow<BroadcastEvent> = callbackFlow {

        val receiver = object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {
                if (intent == null) return

                val type = when (intent.action) {

                    Intent.ACTION_POWER_CONNECTED ->
                        "Зарядний пристрій підключено"

                    Intent.ACTION_POWER_DISCONNECTED ->
                        "Зарядний пристрій відключено"

                    Intent.ACTION_AIRPLANE_MODE_CHANGED ->
                        "Змінено режим «У літаку»"

                    Intent.ACTION_BATTERY_LOW ->
                        "Низький рівень батареї"

                    Intent.ACTION_HEADSET_PLUG ->
                        "Змінено підключення навушників"

                    else ->
                        intent.action ?: "Невідома подія"
                }

                val timestamp = SimpleDateFormat(
                    "dd.MM.yyyy HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                val extras = intent.extras
                    ?.keySet()
                    ?.joinToString("\n") { key ->
                        "$key = ${intent.extras?.get(key)}"
                    }
                    ?: "Додаткових параметрів немає"

                val event = BroadcastEvent(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    timestamp = timestamp,
                    details = extras
                )

                trySend(event)
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_HEADSET_PLUG)
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
}