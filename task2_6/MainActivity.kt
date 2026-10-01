package com.example.timermvvm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.timermvvm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.timerLiveData.observe(this) { seconds ->
            binding.textTimer.text = seconds.toString()
        }

        viewModel.isTimerRunning.observe(this) { isRunning ->
            binding.btnStart.isEnabled = !isRunning
        }

        binding.btnStart.setOnClickListener {

            val seconds =
                binding.editSeconds.text
                    .toString()
                    .toLongOrNull()
                    ?: 30L

            viewModel.startTimer(seconds)
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetTimer()
        }
    }
}