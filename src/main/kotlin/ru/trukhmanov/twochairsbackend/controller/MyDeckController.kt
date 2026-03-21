package ru.trukhmanov.twochairsbackend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto
import ru.trukhmanov.twochairsbackend.dto.game.deck.CreateDeckRequest
import ru.trukhmanov.twochairsbackend.dto.game.deck.CreateQuestionRequest
import ru.trukhmanov.twochairsbackend.dto.game.deck.DeckDto
import ru.trukhmanov.twochairsbackend.dto.game.deck.MyDeckPickDto
import ru.trukhmanov.twochairsbackend.dto.game.deck.UpdateDeckRequest
import ru.trukhmanov.twochairsbackend.service.MyDeckService
import ru.trukhmanov.twochairsbackend.util.CurrentUser

@RestController
@RequestMapping("/decks/my")
@Tag(name = "My Decks", description = "Управление своими колодами")
@SecurityRequirement(name = "bearerAuth")
class MyDeckController(
    private val myDeckService: MyDeckService
) {

    @Operation(summary = "Мои колоды")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Список колод")])
    @GetMapping
    fun myDecks(): List<DeckDto> {
        val userId = CurrentUser.id()
        return myDeckService.myDecks(userId)
    }

    @Operation(summary = "Пикер: куда можно сохранить вопрос", description = "Минимальное представление USER колод")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Список колод")])
    @GetMapping("/picker")
    fun picker(): List<MyDeckPickDto> {
        val userId = CurrentUser.id()
        return myDeckService.picker(userId)
    }

    @Operation(summary = "Создать колоду")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Колода создана"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping
    fun create(@RequestBody req: CreateDeckRequest): DeckDto {
        val userId = CurrentUser.id()
        return myDeckService.createDeck(userId, req)
    }

    @Operation(summary = "Обновить колоду")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Колода обновлена"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PatchMapping("/{deckId}")
    fun update(
        @Parameter(description = "ID колоды") @PathVariable deckId: Long,
        @RequestBody req: UpdateDeckRequest
    ): DeckDto {
        val userId = CurrentUser.id()
        return myDeckService.updateDeck(userId, deckId, req)
    }

    @Operation(summary = "Опубликовать колоду")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Опубликована"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping("/{deckId}/publish")
    fun publish(@Parameter(description = "ID колоды") @PathVariable deckId: Long): ResponseEntity<Void> {
        val userId = CurrentUser.id()
        myDeckService.publish(userId, deckId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Добавить новый вопрос в колоду", description = "Создаёт новый вопрос")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Вопрос добавлен"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping("/{deckId}/questions")
    fun addQuestion(
        @Parameter(description = "ID колоды") @PathVariable deckId: Long,
        @RequestBody req: CreateQuestionRequest
    ): Map<String, Long> {
        val userId = CurrentUser.id()
        val questionId = myDeckService.addQuestion(userId, deckId, req)
        return mapOf("questionId" to questionId)
    }

    @Operation(summary = "Добавить существующий вопрос в колоду", description = "Добавляет существующий вопрос в колоду")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Вопрос добавлен"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping("/{deckId}/questions/{questionId}")
    fun addExistingQuestion(
        @Parameter(description = "ID колоды") @PathVariable deckId: Long,
        @Parameter(description = "ID вопроса") @PathVariable questionId: Long
    ): ResponseEntity<Void> {
        val userId = CurrentUser.id()
        myDeckService.addExistingQuestion(userId, deckId, questionId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Список вопросов колоды")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список вопросов"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @GetMapping("/{deckId}/questions")
    fun questions(@Parameter(description = "ID колоды") @PathVariable deckId: Long): List<QuestionDto> {
        val userId = CurrentUser.id()
        return myDeckService.listQuestions(userId, deckId)
    }

    @Operation(summary = "Удалить вопрос из колоды", description = "Удаляет вопрос из колоды, не удаляя сам вопрос их базы")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Удалено"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/{deckId}/questions/{questionId}")
    fun removeQuestion(
        @Parameter(description = "ID колоды") @PathVariable deckId: Long,
        @Parameter(description = "ID вопроса") @PathVariable questionId: Long
    ): ResponseEntity<String> {
        val userId = CurrentUser.id()
        myDeckService.removeQuestion(userId, deckId, questionId)
        return ResponseEntity.ok("ok")
    }
}
