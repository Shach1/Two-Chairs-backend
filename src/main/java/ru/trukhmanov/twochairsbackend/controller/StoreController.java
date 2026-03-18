package ru.trukhmanov.twochairsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse;
import ru.trukhmanov.twochairsbackend.dto.store.ProductDto;
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseRequest;
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseResponse;
import ru.trukhmanov.twochairsbackend.service.StoreService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/store")
@Tag(name = "Store", description = "Магазин колод и привелегий")
@SecurityRequirement(name = "bearerAuth")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @Operation(summary = "Список доступных позиций", description = "Возвращает позиции, доступные к покупке.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список позиций")
    })
    @GetMapping("/products")
    public List<ProductDto> products() {
        return storeService.products();
    }

    @Operation(summary = "Покупка позиции", description = "Создаёт покупку позиции текущим пользователем.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Покупка выполнена"),
            @ApiResponse(responseCode = "400", description = "Ошибка",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))    })
    @PostMapping("/purchase")
    public PurchaseResponse purchase(@RequestBody PurchaseRequest req) {
        long userId = CurrentUser.id();
        return storeService.purchase(userId, req.productId());
    }
}