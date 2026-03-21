package ru.trukhmanov.twochairsbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.trukhmanov.twochairsbackend.entity.AuthSmsCode
import java.util.Optional

interface AuthSmsCodeRepository : JpaRepository<AuthSmsCode, Long> {

    @Query(
        """
        select c from AuthSmsCode c
        where c.phoneNumber = :phone and c.consumedAt is null
        order by c.createdAt desc
        limit 1
        """
    )
    fun findLatestActiveByPhone(@Param("phone") phone: String): Optional<AuthSmsCode>
}
