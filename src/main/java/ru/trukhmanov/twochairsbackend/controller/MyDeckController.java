package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trukhmanov.twochairsbackend.dto.game.deck.*;
import ru.trukhmanov.twochairsbackend.service.MyDeckService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/decks/my")
public class MyDeckController {

    private final MyDeckService myDeckService;

    public MyDeckController(MyDeckService myDeckService) {
        this.myDeckService = myDeckService;
    }

    @GetMapping
    public List<DeckDto> myDecks() {
        long userId = CurrentUser.id();
        return myDeckService.myDecks(userId);
    }

    @PostMapping
    public DeckDto create(@RequestBody CreateDeckRequest req) {
        long userId = CurrentUser.id();
        return myDeckService.createDeck(userId, req);
    }

    @PatchMapping("/{deckId}")
    public DeckDto update(@PathVariable long deckId, @RequestBody UpdateDeckRequest req) {
        long userId = CurrentUser.id();
        return myDeckService.updateDeck(userId, deckId, req);
    }

    @PostMapping("/{deckId}/publish")
    public ResponseEntity<String> publish(@PathVariable long deckId) {
        long userId = CurrentUser.id();
        myDeckService.publish(userId, deckId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{deckId}/questions")
    public Map<String, Long> addQuestion(@PathVariable long deckId, @RequestBody CreateQuestionRequest req) {
        long userId = CurrentUser.id();
        long questionId = myDeckService.addQuestion(userId, deckId, req);
        return Map.of("questionId", questionId);
    }

    @PostMapping("/{deckId}/questions/{questionId}")
    public ResponseEntity<String> addExistingQuestion(@PathVariable long deckId, @PathVariable long questionId) {
        long userId = CurrentUser.id();
        myDeckService.addExistingQuestion(userId, deckId, questionId);
        return ResponseEntity.ok().build();
    }
}