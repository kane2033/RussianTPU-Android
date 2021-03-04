package ru.tpu.russiantpu.dto

import ru.tpu.russiantpu.main.items.FeedItem

data class FeedItemListDTO(
        val articles: List<FeedItem>,
        val title: String)