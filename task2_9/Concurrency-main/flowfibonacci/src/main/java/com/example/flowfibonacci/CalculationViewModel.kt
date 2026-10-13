package com.example.flowfibonacci

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class CalculationViewModel : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData(Calculated())
    val state: LiveData<State>
        get() = _state

    private suspend fun fib(n: Long): Long {
        var f2 = 1L  // 0-й член послідовності Фібоначчі
        var f1 = 1L  // 1-й член послідовності Фібоначчі

        for (i in 2.. n) {  // рахуємо починаючи з 2-го
            val f = f2 + f1  // поточний член послідовності Фібоначчі
            yield() // <== корутина буде перевіряти чи вона скасована
            f2 = f1
            f1 = f

        }
        return f1
    }


    private var calculationJob: Job? = null
    fun calculate(n: String?) {

        if (n.isNullOrEmpty()) {  // n = null or n = ""
            _state.value = Error //State(error = true)
            return
        }

        calculationJob = viewModelScope.launch {
            _state.value = Progress //State(progress = true)


            val result = withContext(Dispatchers.Default) {
                fib(n.toLong())
            }

            _state.value = Calculated(result) //State(calculated = result)
        }

    }

    fun cancel() {
        calculationJob?.cancel()
        calculationJob = null

        _state.value = Canceled   //State(canceled = true)
    }


}