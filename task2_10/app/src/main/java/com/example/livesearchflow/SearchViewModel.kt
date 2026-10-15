package com.example.livesearchflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchViewModel : ViewModel() {

    private val searchRepository = SearchRepository()

    private val searchQueryFlow = MutableStateFlow("")

    val searchResults = searchQueryFlow
        .debounce(400)
        .filter { query ->
            query.trim().length >= 3
        }
        .distinctUntilChanged()
        .map { query ->
            searchRepository.search(query)
        }
        .flowOn(Dispatchers.IO)
        .catch {
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChanged(query: String) {
        searchQueryFlow.value = query
    }
}