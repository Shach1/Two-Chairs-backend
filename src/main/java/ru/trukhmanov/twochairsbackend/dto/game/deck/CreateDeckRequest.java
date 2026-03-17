package ru.trukhmanov.twochairsbackend.dto.game.deck;

public record CreateDeckRequest(
        String title,
        String description,
        int ageRating
) {}