package com.example.livesearchflow

import android.util.Log

class SearchRepository {

    private val programmingLanguages = listOf(
        "Kotlin",
        "Java",
        "Python",
        "C++",
        "C#",
        "JavaScript",
        "TypeScript",
        "Swift",
        "Go",
        "Rust",
        "Ruby",
        "PHP",
        "Dart",
        "R",
        "Scala"
    )

    fun search(query: String): List<String> {

        Log.d("LIVE_SEARCH", "Виконується пошук: $query")

        // Імітуємо важкий запит
        Thread.sleep(500)

        return programmingLanguages.filter { language ->
            language.contains(query, ignoreCase = true)
        }
    }
}