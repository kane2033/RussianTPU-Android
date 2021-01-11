package ru.tpu.russiantpu.dto

import java.util.*

data class EventDTO(
        val id: String,
        val title: String,
        val description: String,
        val timestamp: Date,
        val eventTarget: String
)