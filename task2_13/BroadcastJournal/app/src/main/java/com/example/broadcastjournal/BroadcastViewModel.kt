package com.example.broadcastjournal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BroadcastViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        BroadcastRepository(application.applicationContext)

    private val _events =
        MutableStateFlow<List<BroadcastEvent>>(emptyList())

    val events: StateFlow<List<BroadcastEvent>> =
        _events.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {

            repository.observeBroadcasts().collect { event ->

                _events.value =
                    listOf(event) + _events.value
            }
        }
    }

    fun deleteEvent(event: BroadcastEvent) {
        _events.value =
            _events.value.filterNot {
                it.id == event.id
            }
    }

    fun clearAll() {
        _events.value = emptyList()
    }
}