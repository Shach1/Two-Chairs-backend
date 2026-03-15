package ru.trukhmanov.twochairsbackend.dto;

public record VerifyCodeRequest(String phoneNumber, String code) {}