package ru.trukhmanov.twochairsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trukhmanov.twochairsbackend.dto.auth.AuthResponse;
import ru.trukhmanov.twochairsbackend.dto.auth.SendCodeRequest;
import ru.trukhmanov.twochairsbackend.dto.auth.SendCodeResponse;
import ru.trukhmanov.twochairsbackend.dto.auth.VerifyCodeRequest;
import ru.trukhmanov.twochairsbackend.service.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Аутентификация по SMS-коду")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @Operation(summary = "Отправить код", description = "Отправляет код на номер телефона и возвращает TTL в секундах.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код отправлен")
    })
    @PostMapping("/send-code")
    public SendCodeResponse sendCode(@RequestBody SendCodeRequest req) {
        int ttl = auth.sendCode(req.phoneNumber());
        return new SendCodeResponse(ttl);
    }

    @Operation(summary = "Подтвердить код", description = "Проверяет код и возвращает токен(ы) авторизации.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код верный"),
            @ApiResponse(responseCode = "400", description = "Код неверный/истёк")
    })
    @PostMapping("/verify-code")
    public AuthResponse verify(@RequestBody VerifyCodeRequest req) {
        return auth.verifyCode(req.phoneNumber(), req.code());
    }
}
