package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    var phoneNumber: String = "",

    @Column(name = "display_name", length = 64)
    var displayName: String? = null,

    @Column(name = "is_premium", nullable = false)
    var premium: Boolean = false,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime? = null
) {

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now()
        }
    }

    companion object {
        fun newUser(phoneNumber: String): User {
            return User(phoneNumber = phoneNumber, premium = false)
        }
    }
}
