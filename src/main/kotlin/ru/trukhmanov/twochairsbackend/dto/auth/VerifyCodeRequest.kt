package ru.trukhmanov.twochairsbackend.dto.auth

data class VerifyCodeRequest(
    val phoneNumber: String,
    val code: String
)
