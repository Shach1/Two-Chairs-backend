package ru.trukhmanov.twochairsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerRequest;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerStatsDto;
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto;
import ru.trukhmanov.twochairsbackend.service.GameService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

@RestController
@RequestMapping("/game")
@Tag(name = "Game", description = "Игровые endpoints")
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Следующий вопрос", description = "Возвращает следующий вопрос для колоды. Если вопросов нет — 204.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вопрос найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/decks/{deckId}/next-question")
    public ResponseEntity<QuestionDto> nextQuestion(@Parameter(description = "ID колоды") @PathVariable long deckId) {
        long userId = CurrentUser.id();
        return gameService.nextQuestion(userId, deckId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Ответить на вопрос", description = "Записывает ответ и возвращает статистику по вопросу (общая для questionId).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответ принят"),
            @ApiResponse(responseCode = "400", description = "Ошибка",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/decks/{deckId}/questions/{questionId}/answer")
    public AnswerStatsDto answer(
            @Parameter(description = "ID колоды") @PathVariable long deckId,
            @Parameter(description = "ID вопроса") @PathVariable long questionId,
            @org.springframework.web.bind.annotation.RequestBody AnswerRequest req
    ) {
        long userId = CurrentUser.id();
        return gameService.answer(userId, deckId, questionId, req.answer());
    }
}