package com.example.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.coroutine.databinding.ActivityMainBinding

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
        Log.d(TAG, "loadData: start data loading from the internet...")

        TODO("Implement following pipeline")
        // on Start:
            // disable button "load data"
            // show progress bar
            // clear City
            // clear Temperature
            // show Toast that data is started loading.

        // on Progress
            // load City
               // and set it into correspondent text view
            // then load temperature for loaded City,
               // and set it into correspondent text view

        // on Finish:
            // hide progress bar
            // enable button "load data"
    }

    private fun loadCity(): String {
        Thread.sleep(3_000)  // to simulate Long-running operation

        return "Kyiv"
    }

    private fun loadTemperature(city: String): Int {
        Thread.sleep(3_000)   // to simulate Long-running operation

        return 15  // Celsius degrees
    }

    companion object {
        val TAG = "XXXX"
    }

}