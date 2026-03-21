package ru.trukhmanov.twochairsbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.trukhmanov.twochairsbackend.entity.User
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByPhoneNumber(phoneNumber: String): Optional<User>
}
