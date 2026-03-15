package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "auth_sms_codes")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AuthSmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="phone_number", nullable=false, length=20)
    private String phoneNumber;

    @Column(name="code", nullable=false, length=10)
    private String code;

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt;

    @Column(name="expires_at", nullable=false)
    private OffsetDateTime expiresAt;

    @Column(name="attempts", nullable=false)
    @Builder.Default
    private int attempts = 0;

    @Column(name="consumed_at")
    private OffsetDateTime consumedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public void consume() {
        this.consumedAt = OffsetDateTime.now();
    }

    public void incAttempts() {
        this.attempts++;
    }

    public static AuthSmsCode newCode(String phoneNumber, String code, OffsetDateTime expiresAt) {
        return AuthSmsCode.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .expiresAt(expiresAt)
                .attempts(0)
                .build();
    }
}