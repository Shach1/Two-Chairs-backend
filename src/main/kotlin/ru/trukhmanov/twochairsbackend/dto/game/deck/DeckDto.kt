package ru.trukhmanov.twochairsbackend.dto.game.deck

data class DeckDto(
    val id: Long,
    val type: String,
    val visibility: String,
    val title: String,
    val description: String?,
    val ageRating: Int,
    val priceRub: Int,
    val locked: Boolean,
    val ownerUserId: Long?
)
