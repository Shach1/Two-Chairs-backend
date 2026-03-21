package ru.trukhmanov.twochairsbackend.dto.store

data class PurchaseResponse(
    val purchaseId: Long,
    val productId: Long,
    val productType: String
)
