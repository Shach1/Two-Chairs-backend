package ru.trukhmanov.twochairsbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ErrorResponse", description = "Единый формат ошибки API")
public record ErrorResponse(
        @Schema(description = "Стабильный код ошибки", example = "DECK_NOT_ACCESSIBLE")
        String errorCode,

        @Schema(description = "Сообщение для пользователя", example = "Deck not accessible")
        String message,

        @Schema(description = "HTTP path запроса", example = "/game/decks/1/next-question")
        String path,

        @Schema(description = "Время ошибки (UTC)", example = "2026-03-18T12:34:56Z")
        Instant timestamp
) {}