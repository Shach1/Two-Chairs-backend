package ru.trukhmanov.twochairsbackend.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.trukhmanov.twochairsbackend.dto.store.ProductDto
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseResponse
import ru.trukhmanov.twochairsbackend.entity.UserPurchase
import ru.trukhmanov.twochairsbackend.repository.ProductRepository
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository
import ru.trukhmanov.twochairsbackend.repository.UserRepository
import java.time.OffsetDateTime

@Service
class StoreService(
    private val productRepository: ProductRepository,
    private val purchaseRepository: UserPurchaseRepository,
    private val userRepository: UserRepository
) {

    fun products(): List<ProductDto> {
        return productRepository.findByActiveTrueOrderByIdAsc()
            .map { product ->
                ProductDto(
                    id = requireNotNull(product.id),
                    type = product.type,
                    title = product.title,
                    priceRub = product.priceRub,
                    deckId = product.deckId,
                    active = product.active
                )
            }
    }

    @Transactional
    fun purchase(userId: Long, productId: Long): PurchaseResponse {
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product not found") }

        if (!product.active) {
            throw IllegalArgumentException("Product is not active")
        }

        if (purchaseRepository.existsByUserIdAndProductId(userId, productId)) {
            throw IllegalArgumentException("Already purchased")
        }

        if (product.type == "DECK" && product.deckId == null) {
            throw IllegalArgumentException("Product deckId is null")
        }

        if (product.type == "PREMIUM") {
            val user = userRepository.findById(userId).orElseThrow()
            user.premium = true
        }

        val purchase = purchaseRepository.save(
            UserPurchase(
                userId = userId,
                productId = productId,
                purchasedAt = OffsetDateTime.now()
            )
        )

        return PurchaseResponse(
            purchaseId = requireNotNull(purchase.id),
            productId = requireNotNull(product.id),
            productType = product.type
        )
    }
}
