package ru.trukhmanov.twochairsbackend.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.trukhmanov.twochairsbackend.dto.auth.AuthResponse
import ru.trukhmanov.twochairsbackend.entity.AuthSmsCode
import ru.trukhmanov.twochairsbackend.entity.User
import ru.trukhmanov.twochairsbackend.repository.AuthSmsCodeRepository
import ru.trukhmanov.twochairsbackend.repository.UserRepository
import ru.trukhmanov.twochairsbackend.security.JwtService
import java.time.OffsetDateTime
import java.util.concurrent.ThreadLocalRandom

@Service
class AuthService(
    private val codeRepository: AuthSmsCodeRepository,
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @Transactional
    fun sendCode(phoneNumber: String): Int {
        val code = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000))
        val ttl = 120
        val expiresAt = OffsetDateTime.now().plusSeconds(ttl.toLong())

        codeRepository.save(AuthSmsCode.newCode(phoneNumber, code, expiresAt))
        log.info("DEV SMS code for {} is {}", phoneNumber, code)

        return ttl
    }

    @Transactional
    fun verifyCode(phoneNumber: String, code: String): AuthResponse {
        val sms = codeRepository.findLatestActiveByPhone(phoneNumber)
            .orElseThrow { IllegalArgumentException("Code not found for this phone number") }

        if (sms.isExpired()) {
            throw IllegalArgumentException("Code expired")
        }

        sms.incAttempts()
        if (sms.code != code) {
            throw IllegalArgumentException("Invalid code")
        }

        sms.consume()

        val user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseGet { userRepository.save(User.newUser(phoneNumber)) }

        val userId = requireNotNull(user.id)
        val token = jwtService.issueToken(userId, user.phoneNumber)

        return AuthResponse(token, AuthResponse.UserDto(userId, user.phoneNumber, user.premium))
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthService::class.java)
    }
}
