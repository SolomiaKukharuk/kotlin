package com.example.broadcastjournal

data class BroadcastEvent(
    val id: String,
    val type: String,
    val timestamp: String,
    val details: String
)