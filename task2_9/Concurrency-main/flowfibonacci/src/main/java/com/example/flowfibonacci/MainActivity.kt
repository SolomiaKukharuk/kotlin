package com.example.flowfibonacci

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.flowfibonacci.databinding.ActivityMainBinding
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<CalculationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        viewModel.state.observe(this) { state ->

            when (state) {
                Error -> {   //state.error  state == Error
                    Toast.makeText(this, "Non valid int value", Toast.LENGTH_SHORT)
                        .show()
                }

                Progress -> {
                    binding.etNum.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnCalculate.isEnabled = false
                    binding.btnCancel.isEnabled = true
                    binding.tvResult.text = ""
                }

                Canceled -> {
                    binding.etNum.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.btnCalculate.isEnabled = true
                    binding.btnCancel.isEnabled = false

                    Toast.makeText(this, "Calculation was canceled", Toast.LENGTH_SHORT)
                        .show()
                }

                is Calculated -> { // calculated
                    binding.etNum.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    binding.btnCalculate.isEnabled = true
                    binding.btnCancel.isEnabled = false
                    binding.tvResult.text = state.value.toString()
                }
            }

        }


        binding.btnCalculate.setOnClickListener {
            val n_str = binding.etNum.text.toString() // "".toLong()

            viewModel.calculate(n_str)
        }

        binding.btnCancel.setOnClickListener {
            viewModel.cancel()
        }

    }

}