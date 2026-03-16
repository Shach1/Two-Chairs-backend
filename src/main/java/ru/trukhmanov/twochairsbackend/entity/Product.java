package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="type", nullable=false, length=32)
    private String type; // DECK/FEATURE_CREATE_DECKS/PREMIUM

    @Column(name="title", nullable=false, length=80)
    private String title;

    @Column(name="price_rub", nullable=false)
    private int priceRub;

    @Column(name="deck_id")
    private Long deckId;

    @Column(name="is_active", nullable=false)
    private boolean active;
}