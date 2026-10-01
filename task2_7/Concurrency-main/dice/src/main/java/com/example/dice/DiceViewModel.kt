package com.example.dice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.random.Random

class DiceViewModel : ViewModel() {

    private val _diceValues =
        MutableLiveData<List<Int>>(listOf(1, 1, 1, 1, 1))

    val diceValues: LiveData<List<Int>>
        get() = _diceValues

    private val _isRolling = MutableLiveData(false)

    val isRolling: LiveData<Boolean>
        get() = _isRolling

    private val runningThreads = mutableListOf<Thread>()

    @Volatile
    private var isCancelled = false

    fun rollDice() {

        if (_isRolling.value == true) return

        _isRolling.value = true
        isCancelled = false

        runningThreads.clear()

        val currentValues =
            (_diceValues.value ?: listOf(1, 1, 1, 1, 1))
                .toMutableList()

        val finishedThreads = AtomicInteger(0)

        for (diceIndex in 0 until 5) {

            val diceThread = thread(start = false) {

                val duration = Random.nextLong(1500L, 3001L)
                val startTime = System.currentTimeMillis()

                try {

                    while (
                        System.currentTimeMillis() - startTime < duration &&
                        !isCancelled
                    ) {

                        val newValue = Random.nextInt(1, 7)

                        synchronized(currentValues) {

                            currentValues[diceIndex] = newValue

                            _diceValues.postValue(
                                currentValues.toList()
                            )
                        }

                        Thread.sleep(100)
                    }

                } catch (e: InterruptedException) {
                    // Потік був зупинений
                }

                if (finishedThreads.incrementAndGet() == 5) {

                    if (!isCancelled) {
                        _isRolling.postValue(false)
                    }
                }
            }

            runningThreads.add(diceThread)
            diceThread.start()
        }
    }

    override fun onCleared() {

        isCancelled = true

        runningThreads.forEach {
            it.interrupt()
        }

        runningThreads.clear()

        super.onCleared()
    }
}