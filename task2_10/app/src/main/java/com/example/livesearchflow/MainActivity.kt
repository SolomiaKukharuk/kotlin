package com.example.livesearchflow

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val etSearchQuery =
            findViewById<EditText>(R.id.etSearchQuery)

        val tvSearchResults =
            findViewById<TextView>(R.id.tvSearchResults)

        val progressBar =
            findViewById<ProgressBar>(R.id.progressBar)

        etSearchQuery.doOnTextChanged { text, _, _, _ ->

            val query = text?.toString().orEmpty()

            viewModel.onQueryChanged(query)

            progressBar.visibility =
                if (query.trim().length >= 3) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        lifecycleScope.launch {

            viewModel.searchResults.collect { results ->

                progressBar.visibility = View.GONE

                tvSearchResults.text =
                    if (results.isEmpty()) {

                        "Нічого не знайдено"

                    } else {

                        results.joinToString(
                            separator = "\n"
                        )
                    }
            }
        }
    }
}