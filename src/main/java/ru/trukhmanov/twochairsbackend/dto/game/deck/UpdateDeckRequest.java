package ru.trukhmanov.twochairsbackend.dto.game.deck;

public record UpdateDeckRequest(
        String title,
        String description,
        Integer ageRating
){}