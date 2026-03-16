package ru.trukhmanov.twochairsbackend.dto.game;

public record QuestionDto(
        long id,
        long deckId,
        String optionA,
        String optionB
) {}