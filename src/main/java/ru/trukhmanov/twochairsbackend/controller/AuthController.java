package ru.trukhmanov.twochairsbackend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trukhmanov.twochairsbackend.dto.AuthResponse;
import ru.trukhmanov.twochairsbackend.dto.SendCodeRequest;
import ru.trukhmanov.twochairsbackend.dto.SendCodeResponse;
import ru.trukhmanov.twochairsbackend.dto.VerifyCodeRequest;
import ru.trukhmanov.twochairsbackend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/send-code")
    public SendCodeResponse sendCode(@RequestBody SendCodeRequest req) {
        int ttl = auth.sendCode(req.phoneNumber());
        return new SendCodeResponse(ttl);
    }

    @PostMapping("/verify-code")
    public AuthResponse verify(@RequestBody VerifyCodeRequest req) {
        return auth.verifyCode(req.phoneNumber(), req.code());
    }
}
