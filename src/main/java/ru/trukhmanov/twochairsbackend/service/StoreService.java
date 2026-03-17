package ru.trukhmanov.twochairsbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.trukhmanov.twochairsbackend.dto.store.ProductDto;
import ru.trukhmanov.twochairsbackend.dto.store.PurchaseResponse;
import ru.trukhmanov.twochairsbackend.entity.Product;
import ru.trukhmanov.twochairsbackend.entity.UserPurchase;
import ru.trukhmanov.twochairsbackend.repository.ProductRepository;
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository;
import ru.trukhmanov.twochairsbackend.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StoreService {

    private final ProductRepository productRepository;
    private final UserPurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    public StoreService(ProductRepository productRepository,
                        UserPurchaseRepository purchaseRepository,
                        UserRepository userRepository) {
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
    }

    public List<ProductDto> products() {
        return productRepository.findByActiveTrueOrderByIdAsc().stream()
                .map(p -> new ProductDto(
                        p.getId(),
                        p.getType(),
                        p.getTitle(),
                        p.getPriceRub(),
                        p.getDeckId(),
                        p.isActive()))
                .toList();
    }

    @Transactional
    public PurchaseResponse purchase(long userId, long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.isActive()) throw new IllegalArgumentException("Product is not active");

        if (purchaseRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Already purchased");
        }

        // минимальная валидация продукта
        if ("DECK".equals(product.getType())) {
            if (product.getDeckId() == null) throw new IllegalArgumentException("Product deckId is null");
        }

        if ("PREMIUM".equals(product.getType())) {
            var user = userRepository.findById(userId).orElseThrow();
            user.setPremium(true);
        }

        UserPurchase purchase = purchaseRepository.save(UserPurchase.builder()
                .userId(userId)
                .productId(productId)
                .purchasedAt(OffsetDateTime.now())
                .build());

        return new PurchaseResponse(purchase.getId(), product.getId(), product.getType());
    }
}