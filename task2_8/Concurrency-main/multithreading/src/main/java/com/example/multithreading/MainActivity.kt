package com.example.multithreading

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.multithreading.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoadData.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {

        // Імітація важкої роботи (15 секунд)
        Thread.sleep(15_000)

        binding.tvResult.text = "Дані завантажено!"
    }
}