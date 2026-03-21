package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "type", nullable = false, length = 32)
    var type: String = "",

    @Column(name = "title", nullable = false, length = 80)
    var title: String = "",

    @Column(name = "price_rub", nullable = false)
    var priceRub: Int = 0,

    @Column(name = "deck_id")
    var deckId: Long? = null,

    @Column(name = "is_active", nullable = false)
    var active: Boolean = false
)
