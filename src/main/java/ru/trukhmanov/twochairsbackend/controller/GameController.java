package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerRequest;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerStatsDto;
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto;
import ru.trukhmanov.twochairsbackend.service.GameService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/decks/{deckId}/next-question")
    public ResponseEntity<QuestionDto> nextQuestion(@PathVariable long deckId) {
        long userId = CurrentUser.id();
        return gameService.nextQuestion(userId, deckId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/decks/{deckId}/questions/{questionId}/answer")
    public AnswerStatsDto answer(@PathVariable long deckId,
                                 @PathVariable long questionId,
                                 @RequestBody AnswerRequest req) {
        long userId = CurrentUser.id();
        return gameService.answer(userId, deckId, questionId, req.answer());
    }
}