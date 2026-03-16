package ru.trukhmanov.twochairsbackend.dto.auth;

public record VerifyCodeRequest(String phoneNumber, String code) {}