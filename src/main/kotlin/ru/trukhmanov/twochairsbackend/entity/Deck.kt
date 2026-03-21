package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "decks")
class Deck(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "type", nullable = false, length = 16)
    var type: String = "",

    @Column(name = "visibility", nullable = false, length = 16)
    var visibility: String = "",

    @Column(name = "title", nullable = false, length = 80)
    var title: String = "",

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "age_rating", nullable = false)
    var ageRating: Int = 0,

    @Column(name = "price_rub", nullable = false)
    var priceRub: Int = 0,

    @Column(name = "owner_user_id")
    var ownerUserId: Long? = null,

    @Column(name = "is_published", nullable = false)
    var published: Boolean = false
)
