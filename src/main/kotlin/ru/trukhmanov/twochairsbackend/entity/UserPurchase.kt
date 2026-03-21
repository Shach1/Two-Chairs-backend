package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "user_purchases")
class UserPurchase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long? = null,

    @Column(name = "product_id", nullable = false)
    var productId: Long? = null,

    @Column(name = "purchased_at", nullable = false)
    var purchasedAt: OffsetDateTime? = null
)
