package ru.trukhmanov.twochairsbackend.dto.game.deck

data class UpdateDeckRequest(
    val title: String?,
    val description: String?,
    val ageRating: Int?
)
