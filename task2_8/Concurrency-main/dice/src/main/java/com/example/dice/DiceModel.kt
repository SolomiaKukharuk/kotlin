package com.example.dice

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

class DiceModel {

    private suspend fun rollSingleDie(
        duration: Long,
        onValueChanged: (Int) -> Unit
    ): Int {

        var value = 1
        var elapsedTime = 0L

        while (elapsedTime < duration) {

            value = Random.nextInt(1, 7)
            onValueChanged(value)

            delay(100)
            elapsedTime += 100
        }

        return value
    }

    suspend fun rollAllDice(
        onDieValueChanged: (index: Int, value: Int) -> Unit
    ): List<Int> = coroutineScope {

        (0 until 5).map { index ->

            async {

                val duration =
                    Random.nextLong(1500L, 3001L)

                rollSingleDie(duration) { value ->
                    onDieValueChanged(index, value)
                }
            }

        }.awaitAll()
    }
}