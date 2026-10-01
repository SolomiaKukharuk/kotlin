package com.example.timermvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    private val timerModel = TimerModel()

    private val _timerLiveData = MutableLiveData<Long>(30L)
    val timerLiveData: LiveData<Long>
        get() = _timerLiveData

    private val _isTimerRunning = MutableLiveData<Boolean>(false)
    val isTimerRunning: LiveData<Boolean>
        get() = _isTimerRunning

    fun startTimer(userSeconds: Long = 30L) {

        _timerLiveData.value = userSeconds
        _isTimerRunning.value = true

        timerModel.start(
            seconds = userSeconds,

            onTick = { secondsLeft ->
                _timerLiveData.value = secondsLeft
            },

            onFinish = {
                _timerLiveData.value = 0L
                _isTimerRunning.value = false
            }
        )
    }

    fun resetTimer() {
        timerModel.stop()

        _timerLiveData.value = 30L
        _isTimerRunning.value = false
    }

    override fun onCleared() {
        timerModel.stop()
        super.onCleared()
    }
}