package ru.tpu.russiantpu.dto

/**
 * Класс хранит документы
 */
data class DocumentDTO(
        val id: String,
        val name: String,
        val loadDate: String,
        val url: String,
        val fileName: String,
        val lastUseDate: String?)