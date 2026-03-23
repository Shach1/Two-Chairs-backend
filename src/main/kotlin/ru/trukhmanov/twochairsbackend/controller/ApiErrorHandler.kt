package ru.trukhmanov.twochairsbackend.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse
import java.time.Instant
import java.util.NoSuchElementException

@RestControllerAdvice
class ApiErrorHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = ex.message ?: "Bad request"
        val code = CODES[message] ?: "BAD_REQUEST"

        log.error("Request failed: {} {} -> {} ({})", req.method, req.requestURI, message, code)

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                errorCode = code,
                message = message,
                path = req.requestURI,
                timestamp = Instant.now()
            )
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(ex: NoSuchElementException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Request failed: {} {} -> not found entity", req.method, req.requestURI)

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                errorCode = "BAD_REQUEST",
                message = "Bad request",
                path = req.requestURI,
                timestamp = Instant.now()
            )
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(ApiErrorHandler::class.java)

        private val CODES = mapOf(
            "Not your deck" to "DECK_NOT_OWNED",
            "Not a user deck" to "DECK_NOT_USER_TYPE",
            "Deck is not published" to "DECK_NOT_PUBLISHED",
            "Deck not accessible" to "DECK_NOT_ACCESSIBLE",
            "Answer must be A or B" to "ANSWER_OPTION_A_OR_B_REQUIRED",
            "Already answered" to "QUESTION_ALREADY_ANSWERED",
            "Question not found" to "QUESTION_NOT_FOUND",
            "Question is not in this deck" to "QUESTION_NOT_IN_DECK",
            "Already added" to "QUESTION_ALREADY_ADDED",
            "Product not found" to "PRODUCT_NOT_FOUND",
            "Product is not active" to "PRODUCT_IS_INACTIVE",
            "Already purchased" to "PRODUCT_ALREADY_PURCHASED",
            "Product deckId is null" to "BAD_PARAMETER",
            "To create your own decks you need PREMIUM or FEATURE_CREATE_DECKS" to "CREATE_DECKS_FEATURE_REQUIRED",
            "Code not found for this phone number" to "SMS_CODE_NOT_FOUND",
            "Code expired" to "SMS_CODE_EXPIRED",
            "Invalid code" to "SMS_CODE_IS_INVALID"
        )
    }
}
