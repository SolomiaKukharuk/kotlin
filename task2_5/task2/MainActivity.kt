package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserTracker.register(this)

        Timber.d(
            "Зареєстровано слухачів у UserTracker: ${UserTracker.getListenersCount()}"
        )

        findViewById<Button>(R.id.buttonSecond).setOnClickListener {
            startActivity(
                Intent(this, SecondActivity::class.java)
            )
        }
    }

    override fun onDestroy() {
        UserTracker.unregister(this)
        super.onDestroy()
    }
}