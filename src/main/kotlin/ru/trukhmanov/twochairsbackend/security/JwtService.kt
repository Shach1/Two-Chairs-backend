package ru.trukhmanov.twochairsbackend.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${spring.security.jwt.secret}") secret: String,
    @param:Value("\${spring.security.jwt.issuer}") private val issuer: String,
    @param:Value("\${spring.security.jwt.accessTokenTtlSeconds}") private val ttlSeconds: Long
) {

    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun issueToken(userId: Long, phoneNumber: String): String {
        val now = Instant.now()
        val exp = now.plusSeconds(ttlSeconds)

        return Jwts.builder()
            .issuer(issuer)
            .subject(userId.toString())
            .claim("phone", phoneNumber)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(key)
            .compact()
    }
}
