package ru.trukhmanov.twochairsbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trukhmanov.twochairsbackend.dto.auth.AuthResponse;
import ru.trukhmanov.twochairsbackend.entity.AuthSmsCode;
import ru.trukhmanov.twochairsbackend.security.JwtService;
import ru.trukhmanov.twochairsbackend.entity.User;
import ru.trukhmanov.twochairsbackend.repository.UserRepository;
import ru.trukhmanov.twochairsbackend.repository.AuthSmsCodeRepository;


import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthSmsCodeRepository codeRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthSmsCodeRepository codeRepository, UserRepository userRepository, JwtService jwtService) {
        this.codeRepository = codeRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public int sendCode(String phoneNumber) {
        String code = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
        int ttl = 120;
        var expiresAt = OffsetDateTime.now().plusSeconds(ttl);

        codeRepository.save(AuthSmsCode.newCode(phoneNumber, code, expiresAt));

        log.info("DEV SMS code for {} is {}", phoneNumber, code);

        return ttl;
    }

    @Transactional
    public AuthResponse verifyCode(String phoneNumber, String code) {
        var sms = codeRepository.findLatestActiveByPhone(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Code not found"));

        if (sms.isExpired()) throw new IllegalArgumentException("Code expired");

        sms.incAttempts();
        if (!sms.getCode().equals(code)) throw new IllegalArgumentException("Invalid code");

        sms.consume();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> userRepository.save(User.newUser(phoneNumber)));

        String token = jwtService.issueToken(user.getId(), user.getPhoneNumber());

        return new AuthResponse(token, new AuthResponse.UserDto(user.getId(), user.getPhoneNumber(), user.isPremium()));
    }
}