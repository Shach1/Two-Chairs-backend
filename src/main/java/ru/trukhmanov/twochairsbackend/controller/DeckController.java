package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trukhmanov.twochairsbackend.dto.game.DeckDto;
import ru.trukhmanov.twochairsbackend.service.DeckService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/decks")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    // Только доступные колоды
    @GetMapping
    public List<DeckDto> accessible() {
        long userId = CurrentUser.id();
        return deckService.getAccessibleDecks(userId);
    }

    // Витрина платных колод (продающая)
    @GetMapping("/store")
    public List<DeckDto> store() {
        long userId = CurrentUser.id();
        return deckService.getStoreDecks(userId);
    }
}