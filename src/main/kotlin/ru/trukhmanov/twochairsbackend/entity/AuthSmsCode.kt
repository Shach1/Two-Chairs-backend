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
@Table(name = "auth_sms_codes")
class AuthSmsCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "phone_number", nullable = false, length = 20)
    var phoneNumber: String = "",

    @Column(name = "code", nullable = false, length = 10)
    var code: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "attempts", nullable = false)
    var attempts: Int = 0,

    @Column(name = "consumed_at")
    var consumedAt: OffsetDateTime? = null
) {

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now()
        }
    }

    fun isExpired(): Boolean = OffsetDateTime.now().isAfter(expiresAt)

    fun isConsumed(): Boolean = consumedAt != null

    fun consume() {
        consumedAt = OffsetDateTime.now()
    }

    fun incAttempts() {
        attempts++
    }

    companion object {
        fun newCode(phoneNumber: String, code: String, expiresAt: OffsetDateTime): AuthSmsCode {
            return AuthSmsCode(
                phoneNumber = phoneNumber,
                code = code,
                expiresAt = expiresAt,
                attempts = 0
            )
        }
    }
}
