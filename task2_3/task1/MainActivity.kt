package com.example.calculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val number1 = findViewById<EditText>(R.id.editTextNumber1)
        val number2 = findViewById<EditText>(R.id.editTextNumber2)

        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val buttonSubtract = findViewById<Button>(R.id.buttonSubtract)
        val buttonMultiply = findViewById<Button>(R.id.buttonMultiply)
        val buttonDivide = findViewById<Button>(R.id.buttonDivide)

        val resultText = findViewById<TextView>(R.id.textViewResult)


        buttonAdd.setOnClickListener {
            calculate(
                number1,
                number2,
                resultText,
                "+"
            )
        }


        buttonSubtract.setOnClickListener {
            calculate(
                number1,
                number2,
                resultText,
                "-"
            )
        }


        buttonMultiply.setOnClickListener {
            calculate(
                number1,
                number2,
                resultText,
                "*"
            )
        }


        buttonDivide.setOnClickListener {
            calculate(
                number1,
                number2,
                resultText,
                "/"
            )
        }
    }


    private fun calculate(
        number1: EditText,
        number2: EditText,
        resultText: TextView,
        operation: String
    ) {

        val firstText = number1.text.toString()
        val secondText = number2.text.toString()

        if (firstText.isEmpty() || secondText.isEmpty()) {

            resultText.text =
                "Будь ласка, введіть обидва числа"

            Log.w(
                "Calculator",
                "Одне або обидва поля порожні"
            )

            return
        }


        val firstNumber = firstText.toDoubleOrNull()
        val secondNumber = secondText.toDoubleOrNull()

        if (firstNumber == null || secondNumber == null) {

            resultText.text =
                "Помилка: введіть коректні числа"

            Log.w(
                "Calculator",
                "Некоректні числові дані"
            )

            return
        }


        Log.d(
            "Calculator",
            "Операція $operation з числами $firstNumber і $secondNumber"
        )


        val result = when (operation) {

            "+" -> firstNumber + secondNumber

            "-" -> firstNumber - secondNumber

            "*" -> firstNumber * secondNumber

            "/" -> {

                if (secondNumber == 0.0) {

                    resultText.text =
                        "Помилка: ділення на нуль неможливе!"

                    Log.w(
                        "Calculator",
                        "Спроба ділення на нуль"
                    )

                    return
                }

                firstNumber / secondNumber
            }

            else -> return
        }


        resultText.text = "Результат: $result"
    }
}