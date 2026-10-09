package com.example.dice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DiceViewModel : ViewModel() {

    private val model = DiceModel()

    private val _diceValues =
        MutableLiveData<List<Int>>(listOf(1, 1, 1, 1, 1))

    val diceValues: LiveData<List<Int>>
        get() = _diceValues

    private val _isRolling = MutableLiveData(false)

    val isRolling: LiveData<Boolean>
        get() = _isRolling

    private val _buttonText = MutableLiveData("ROLL")

    val buttonText: LiveData<String>
        get() = _buttonText

    private var rollJob: Job? = null

    fun rollDice() {

        rollJob?.cancel()

        _isRolling.value = true
        _buttonText.value = "RESTART"

        rollJob = viewModelScope.launch {

            val currentValues =
                (_diceValues.value ?: listOf(1, 1, 1, 1, 1))
                    .toMutableList()

            model.rollAllDice { index, value ->

                currentValues[index] = value
                _diceValues.value = currentValues.toList()
            }

            _isRolling.value = false
            _buttonText.value = "ROLL"
        }
    }
}