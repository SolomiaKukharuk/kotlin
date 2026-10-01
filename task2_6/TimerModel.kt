package com.example.timermvvm

import android.os.CountDownTimer

class TimerModel {

    private var countDownTimer: CountDownTimer? = null

    fun start(
        seconds: Long = 30L,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        countDownTimer?.cancel()

        onTick(seconds)

        countDownTimer = object : CountDownTimer(
            seconds * 1000,
            1000
        ) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                onTick(secondsLeft)
            }

            override fun onFinish() {
                onTick(0L)
                onFinish()
            }
        }.start()
    }

    fun stop() {
        countDownTimer?.cancel()
        countDownTimer = null
    }
}