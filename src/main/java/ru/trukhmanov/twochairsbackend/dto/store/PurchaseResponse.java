package ru.trukhmanov.twochairsbackend.dto.store;

public record PurchaseResponse(
        long purchaseId,
        long productId,
        String productType
) {}