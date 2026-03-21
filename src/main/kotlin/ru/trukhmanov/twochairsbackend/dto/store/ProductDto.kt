package ru.trukhmanov.twochairsbackend.dto.store

data class ProductDto(
    val id: Long,
    val type: String,
    val title: String,
    val priceRub: Int,
    val deckId: Long?,
    val active: Boolean
)
