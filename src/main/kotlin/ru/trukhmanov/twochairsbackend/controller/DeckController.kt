package ru.trukhmanov.twochairsbackend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.trukhmanov.twochairsbackend.dto.game.deck.DeckDto
import ru.trukhmanov.twochairsbackend.service.DeckService
import ru.trukhmanov.twochairsbackend.util.CurrentUser

@RestController
@RequestMapping("/decks")
@Tag(name = "Decks", description = "Доступные колоды и витрина")
@SecurityRequirement(name = "bearerAuth")
class DeckController(
    private val deckService: DeckService
) {

    @Operation(summary = "Доступные колоды", description = "Возвращает колоды, к которым у текущего пользователя есть доступ.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список колод")
        ]
    )
    @GetMapping
    fun accessible(): List<DeckDto> {
        val userId = CurrentUser.id()
        return deckService.getAccessibleDecks(userId)
    }

    @Operation(summary = "Витрина платных колод", description = "Возвращает колоды для покупки/промо-выдачи.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список колод")
        ]
    )
    @GetMapping("/store")
    fun store(): List<DeckDto> {
        val userId = CurrentUser.id()
        return deckService.getStoreDecks(userId)
    }
}
