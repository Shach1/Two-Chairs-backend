package ru.trukhmanov.twochairsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto;
import ru.trukhmanov.twochairsbackend.dto.game.deck.*;
import ru.trukhmanov.twochairsbackend.service.MyDeckService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/decks/my")
@Tag(name = "My Decks", description = "Управление своими колодами")
@SecurityRequirement(name = "bearerAuth")
public class MyDeckController {

    private final MyDeckService myDeckService;

    public MyDeckController(MyDeckService myDeckService) {
        this.myDeckService = myDeckService;
    }

    @Operation(summary = "Мои колоды")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Список колод"))
    @GetMapping
    public List<DeckDto> myDecks() {
        long userId = CurrentUser.id();
        return myDeckService.myDecks(userId);
    }

    @Operation(summary = "Пикер: куда можно сохранить вопрос", description = "Минимальное представление USER колод")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Список колод"))
    @GetMapping("/picker")
    public List<MyDeckPickDto> picker() {
        long userId = CurrentUser.id();
        return myDeckService.picker(userId);
    }

    @Operation(summary = "Создать колоду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Колода создана"),
            @ApiResponse(responseCode = "400", description = "Нет привилегии для этого действия")
    })
    @PostMapping
    public DeckDto create(@RequestBody CreateDeckRequest req) {
        long userId = CurrentUser.id();
        return myDeckService.createDeck(userId, req);
    }

    @Operation(summary = "Обновить колоду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Колода обновлена"),
            @ApiResponse(responseCode = "400", description = "Нет доступа")
    })
    @PatchMapping("/{deckId}")
    public DeckDto update(
            @Parameter(description = "ID колоды") @PathVariable long deckId,
            @RequestBody UpdateDeckRequest req
    ) {
        long userId = CurrentUser.id();
        return myDeckService.updateDeck(userId, deckId, req);
    }

    @Operation(summary = "Опубликовать колоду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Опубликована"),
            @ApiResponse(responseCode = "400", description = "Нет доступа")
    })
    @PostMapping("/{deckId}/publish")
    public ResponseEntity<String> publish(@Parameter(description = "ID колоды") @PathVariable long deckId) {
        long userId = CurrentUser.id();
        myDeckService.publish(userId, deckId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Добавить новый вопрос в колоду", description = "Создаёт новый вопрос")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вопрос добавлен"),
            @ApiResponse(responseCode = "400", description = "Уже добавлен / нет доступа")
    })
    @PostMapping("/{deckId}/questions")
    public Map<String, Long> addQuestion(
            @Parameter(description = "ID колоды") @PathVariable long deckId,
            @RequestBody CreateQuestionRequest req
    ) {
        long userId = CurrentUser.id();
        long questionId = myDeckService.addQuestion(userId, deckId, req);
        return Map.of("questionId", questionId);
    }

    @Operation(summary = "Добавить существующий вопрос в колоду", description = "Добавляет существующий вопрос в колоду")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вопрос добавлен"),
            @ApiResponse(responseCode = "400", description = "Уже добавлен / нет доступа")
    })
    @PostMapping("/{deckId}/questions/{questionId}")
    public ResponseEntity<String> addExistingQuestion(
            @Parameter(description = "ID колоды") @PathVariable long deckId,
            @Parameter(description = "ID вопроса") @PathVariable long questionId
    ) {
        long userId = CurrentUser.id();
        myDeckService.addExistingQuestion(userId, deckId, questionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Список вопросов колоды")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список вопросов"),
            @ApiResponse(responseCode = "400", description = "Нет доступа к колоде")
    })
    @GetMapping("/{deckId}/questions")
    public List<QuestionDto> questions(@Parameter(description = "ID колоды") @PathVariable long deckId) {
        long userId = CurrentUser.id();
        return myDeckService.listQuestions(userId, deckId);
    }

    @Operation(summary = "Удалить вопрос из колоды", description = "Удаляет вопрос из колоды, не удаляя сам вопрос их базы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Удалено"),
            @ApiResponse(responseCode = "400", description = "Нет доступа")
    })
    @DeleteMapping("/{deckId}/questions/{questionId}")
    public ResponseEntity<String> removeQuestion(
            @Parameter(description = "ID колоды") @PathVariable long deckId,
            @Parameter(description = "ID вопроса") @PathVariable long questionId
    ) {
        long userId = CurrentUser.id();
        myDeckService.removeQuestion(userId, deckId, questionId);
        return ResponseEntity.ok("ok");
    }
}