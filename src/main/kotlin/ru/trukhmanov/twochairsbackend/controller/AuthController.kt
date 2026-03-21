package ru.trukhmanov.twochairsbackend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse
import ru.trukhmanov.twochairsbackend.dto.auth.AuthResponse
import ru.trukhmanov.twochairsbackend.dto.auth.SendCodeRequest
import ru.trukhmanov.twochairsbackend.dto.auth.SendCodeResponse
import ru.trukhmanov.twochairsbackend.dto.auth.VerifyCodeRequest
import ru.trukhmanov.twochairsbackend.service.AuthService

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Аутентификация по SMS-коду")
class AuthController(
    private val auth: AuthService
) {

    @Operation(summary = "Отправить код", description = "Отправляет код на номер телефона и возвращает TTL в секундах.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Код отправлен")
        ]
    )
    @PostMapping("/send-code")
    fun sendCode(@RequestBody req: SendCodeRequest): SendCodeResponse {
        val ttl = auth.sendCode(req.phoneNumber)
        return SendCodeResponse(ttl)
    }

    @Operation(summary = "Подтвердить код", description = "Проверяет код и возвращает токен(ы) авторизации.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Код верный"),
            ApiResponse(
                responseCode = "400",
                description = "Ошибка",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping("/verify-code")
    fun verify(@RequestBody req: VerifyCodeRequest): AuthResponse {
        return auth.verifyCode(req.phoneNumber, req.code)
    }
}
