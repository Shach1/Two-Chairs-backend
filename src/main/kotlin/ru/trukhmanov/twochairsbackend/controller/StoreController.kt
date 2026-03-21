package ru.trukhmanov.twochairsbackend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse
import ru.trukhmanov.twochairsbackend.dto.store.ProductDto
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseRequest
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseResponse
import ru.trukhmanov.twochairsbackend.service.StoreService
import ru.trukhmanov.twochairsbackend.util.CurrentUser

@RestController
@RequestMapping("/store")
@Tag(name = "Store", description = "Магазин колод и привелегий")
@SecurityRequirement(name = "bearerAuth")
class StoreController(
    private val storeService: StoreService
) {

    @Operation(summary = "Список доступных позиций", description = "Возвращает позиции, доступные к покупке.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список позиций")
        ]
    )
    @GetMapping("/products")
    fun products(): List<ProductDto> {
        return storeService.products()
    }

    @Operation(summary = "Покупка позиции", description = "Создаёт покупку позиции текущим пользователем.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Покупка выполнена"),
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
    @PostMapping("/purchase")
    fun purchase(@RequestBody req: PurchaseRequest): PurchaseResponse {
        val userId = CurrentUser.id()
        return storeService.purchase(userId, req.productId)
    }
}
