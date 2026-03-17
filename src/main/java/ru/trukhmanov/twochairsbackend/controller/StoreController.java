package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.web.bind.annotation.*;
import ru.trukhmanov.twochairsbackend.dto.store.ProductDto;
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseRequest;
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseResponse;
import ru.trukhmanov.twochairsbackend.service.StoreService;
import ru.trukhmanov.twochairsbackend.util.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/products")
    public List<ProductDto> products() {
        return storeService.products();
    }

    @PostMapping("/purchase")
    public PurchaseResponse purchase(@RequestBody PurchaseRequest req) {
        long userId = CurrentUser.id();
        return storeService.purchase(userId, req.productId());
    }
}