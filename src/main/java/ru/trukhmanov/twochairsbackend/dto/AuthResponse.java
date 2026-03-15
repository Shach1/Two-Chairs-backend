package ru.trukhmanov.twochairsbackend.dto;

public record AuthResponse(String accessToken, UserDto user) {
    public record UserDto(long id, String phoneNumber, boolean isPremium) {}
}