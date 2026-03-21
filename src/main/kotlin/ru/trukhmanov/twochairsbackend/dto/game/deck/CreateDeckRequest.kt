package ru.trukhmanov.twochairsbackend.dto.game.deck

data class CreateDeckRequest(
    val title: String,
    val description: String?,
    val ageRating: Int
)
