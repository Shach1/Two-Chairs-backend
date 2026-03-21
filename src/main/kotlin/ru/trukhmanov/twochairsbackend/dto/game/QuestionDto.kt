package ru.trukhmanov.twochairsbackend.dto.game

data class QuestionDto(
    val id: Long,
    val deckId: Long,
    val optionA: String,
    val optionB: String
)
