package com.example.cancellation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class MainActivity : AppCompatActivity() {

    private var calculationJob: Job? = null

    companion object {
        private const val TAG = "COROUTINES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ceh = CoroutineExceptionHandler { _, throwable ->
            Log.e(
                TAG,
                "Глобальне перехоплення крашу: ${throwable.message}"
            )
        }

        // БЛОК 1 — кооперативне скасування
        findViewById<Button>(R.id.btnStart)
            .setOnClickListener {

                calculationJob = lifecycleScope.launch(Dispatchers.Default) {

                    try {

                        for (i in 1..100) {

                            Thread.sleep(50)

                            ensureActive()

                            Log.d(TAG, "Обробка: $i%")
                        }

                        Log.d(
                            TAG,
                            "Роботу успішно завершено!"
                        )

                    } catch (e: CancellationException) {

                        Log.d(
                            TAG,
                            "Обробку скасовано користувачем!"
                        )

                        throw e
                    }
                }
            }

        findViewById<Button>(R.id.btnCancel)
            .setOnClickListener {

                calculationJob?.cancel()
            }

        // БЛОК 3 — supervisorScope
        findViewById<Button>(R.id.btnTestCrash)
            .setOnClickListener {

                lifecycleScope.launch {

                    supervisorScope {

                        launch {

                            try {

                                delay(1000)

                                throw RuntimeException(
                                    "Банер не завантажився!"
                                )

                            } catch (e: Exception) {

                                Log.e(
                                    TAG,
                                    "Банер не вдалося завантажити: ${e.message}"
                                )
                            }
                        }

                        launch {

                            delay(2000)

                            Log.d(
                                TAG,
                                "Стрічка новин завантажена"
                            )
                        }
                    }
                }
            }

        // БЛОК 4 — CoroutineExceptionHandler
        findViewById<Button>(R.id.btnGlobalCrash)
            .setOnClickListener {

                val safeScope = CoroutineScope(
                    SupervisorJob() +
                            Dispatchers.Main +
                            ceh
                )

                safeScope.launch {

                    delay(500)

                    throw IllegalArgumentException(
                        "Ой!"
                    )
                }
            }

        // ДОДАТКОВЕ ЗАВДАННЯ ⭐
        findViewById<Button>(R.id.btnNestedCrash)
            .setOnClickListener {

                lifecycleScope.launch {

                    launch(ceh) {

                        delay(500)

                        throw RuntimeException(
                            "Вкладений краш!"
                        )
                    }
                }
            }
    }
}