package ru.trukhmanov.twochairsbackend.util

import org.springframework.security.core.context.SecurityContextHolder

object CurrentUser {
    fun id(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal ?: throw IllegalStateException("Unauthenticated")
        return principal.toString().toLong()
    }
}
