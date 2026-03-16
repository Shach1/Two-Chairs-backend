package ru.trukhmanov.twochairsbackend.dto.game;

public record DeckDto(
        long id,
        String type,          // DEFAULT/PAID/USER
        String visibility,    // PRIVATE/PUBLIC
        String title,
        String description,
        int ageRating,
        int priceRub,
        boolean locked,
        Long ownerUserId
) {}