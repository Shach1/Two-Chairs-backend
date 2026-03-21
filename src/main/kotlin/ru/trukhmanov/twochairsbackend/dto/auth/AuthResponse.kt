package ru.trukhmanov.twochairsbackend.dto.auth

data class AuthResponse(
    val accessToken: String,
    val user: UserDto
) {
    data class UserDto(
        val id: Long,
        val phoneNumber: String,
        val isPremium: Boolean
    )
}
