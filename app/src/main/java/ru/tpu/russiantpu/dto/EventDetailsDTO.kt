package ru.tpu.russiantpu.dto

import java.util.*

data class EventDetailsDTO(
        val id: String,
        val title: String,
        val description: String,
        val timestamp: Date,
        val eventTarget: String,
        val detailedMessage: String?,
        val onlineMeetingLink: String?
) {
}