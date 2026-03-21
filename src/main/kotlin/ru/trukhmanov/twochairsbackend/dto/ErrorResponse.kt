package ru.trukhmanov.twochairsbackend.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "ErrorResponse", description = "Единый формат ошибки API")
data class ErrorResponse(
    @field:Schema(description = "Стабильный код ошибки", example = "DECK_NOT_ACCESSIBLE")
    val errorCode: String,

    @field:Schema(description = "Сообщение для пользователя", example = "Deck not accessible")
    val message: String,

    @field:Schema(description = "HTTP path запроса", example = "/game/decks/1/next-question")
    val path: String,

    @field:Schema(description = "Время ошибки (UTC)", example = "2026-03-18T12:34:56Z")
    val timestamp: Instant
)
