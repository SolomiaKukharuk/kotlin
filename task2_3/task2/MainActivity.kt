package com.example.full_calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.full_calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var firstNumber: Double? = null
    private var operation: String? = null
    private var startNewNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberButtons()
        setupOperationButtons()

        binding.buttonDecimal.setOnClickListener {
            addDecimalPoint()
        }

        binding.buttonClear.setOnClickListener {
            clearCalculator()
        }

        binding.buttonBackspace.setOnClickListener {
            backspace()
        }

        binding.buttonEquals.setOnClickListener {
            calculateResult()
        }
    }


    private fun setupNumberButtons() {

        val buttons = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )

        buttons.forEach { button ->

            button.setOnClickListener {
                addDigit(button.text.toString())
            }
        }
    }


    private fun addDigit(digit: String) {

        if (
            startNewNumber ||
            binding.textDisplay.text.toString() ==
            getString(R.string.error)
        ) {

            binding.textDisplay.text = digit

            startNewNumber = false

            return
        }


        val current =
            binding.textDisplay.text.toString()


        if (current == "0") {

            binding.textDisplay.text = digit

        } else {

            binding.textDisplay.append(digit)
        }
    }


    private fun setupOperationButtons() {

        binding.buttonAdd.setOnClickListener {
            selectOperation("+")
        }

        binding.buttonSubtract.setOnClickListener {
            selectOperation("-")
        }

        binding.buttonMultiply.setOnClickListener {
            selectOperation("*")
        }

        binding.buttonDivide.setOnClickListener {
            selectOperation("/")
        }
    }


    private fun selectOperation(newOperation: String) {

        val currentNumber =
            binding.textDisplay.text
                .toString()
                .toDoubleOrNull()
                ?: return


        if (
            firstNumber != null &&
            operation != null &&
            !startNewNumber
        ) {

            val result = calculate(
                firstNumber!!,
                currentNumber,
                operation!!
            )


            if (result == null) {

                showError()

                return
            }


            firstNumber = result

            binding.textDisplay.text =
                formatNumber(result)

        } else {

            firstNumber = currentNumber
        }


        operation = newOperation

        startNewNumber = true
    }


    private fun calculateResult() {

        val first =
            firstNumber ?: return

        val currentOperation =
            operation ?: return


        if (startNewNumber) {
            return
        }


        val second =
            binding.textDisplay.text
                .toString()
                .toDoubleOrNull()
                ?: return


        val result =
            calculate(
                first,
                second,
                currentOperation
            )


        if (result == null) {

            showError()

            return
        }


        binding.textDisplay.text =
            formatNumber(result)


        firstNumber = null
        operation = null

        startNewNumber = true
    }


    private fun calculate(
        first: Double,
        second: Double,
        operation: String
    ): Double? {

        return when (operation) {

            "+" -> first + second

            "-" -> first - second

            "*" -> first * second

            "/" -> {

                if (second == 0.0) {
                    null
                } else {
                    first / second
                }
            }

            else -> null
        }
    }


    private fun addDecimalPoint() {

        if (
            startNewNumber ||
            binding.textDisplay.text.toString() ==
            getString(R.string.error)
        ) {

            binding.textDisplay.text = "0."

            startNewNumber = false

            return
        }


        if (!binding.textDisplay.text.contains(".")) {

            binding.textDisplay.append(".")
        }
    }


    private fun backspace() {

        if (
            binding.textDisplay.text.toString() ==
            getString(R.string.error)
        ) {

            clearCalculator()

            return
        }


        if (startNewNumber) {
            return
        }


        val current =
            binding.textDisplay.text.toString()


        if (current.length > 1) {

            binding.textDisplay.text =
                current.dropLast(1)

        } else {

            binding.textDisplay.text =
                getString(R.string.display_zero)

            startNewNumber = true
        }
    }


    private fun clearCalculator() {

        binding.textDisplay.text =
            getString(R.string.display_zero)

        firstNumber = null

        operation = null

        startNewNumber = true
    }


    private fun showError() {

        binding.textDisplay.text =
            getString(R.string.error)

        firstNumber = null

        operation = null

        startNewNumber = true
    }


    private fun formatNumber(number: Double): String {

        return if (number % 1.0 == 0.0) {

            number.toLong().toString()

        } else {

            number.toString()
        }
    }
}