package ru.trukhmanov.twochairsbackend.dto.store;

public record ProductDto(
        long id,
        String type,      // DECK / FEATURE_CREATE_DECKS / PREMIUM
        String title,
        int priceRub,
        Long deckId,
        boolean active
) {}