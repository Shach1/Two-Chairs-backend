package ru.trukhmanov.twochairsbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.trukhmanov.twochairsbackend.dto.ErrorResponse;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiErrorHandler.class);

    private static final Map<String, String> CODES = Map.ofEntries(
            Map.entry("Not your deck", "DECK_NOT_OWNED"),
            Map.entry("Not a user deck", "DECK_NOT_USER_TYPE"),
            Map.entry("Deck is not published", "DECK_NOT_PUBLISHED"),
            Map.entry("Deck not accessible", "DECK_NOT_ACCESSIBLE"),
            Map.entry("Answer must be A or B", "ANSWER_OPTION_A_OR_B_REQUIRED"),
            Map.entry("Already answered", "QUESTION_ALREADY_ANSWERED"),
            Map.entry("Question not found", "QUESTION_NOT_FOUND"),
            Map.entry("Question is not in this deck", "QUESTION_NOT_IN_DECK"),
            Map.entry("Already added", "QUESTION_ALREADY_ADDED"),
            Map.entry("Product not found", "PRODUCT_NOT_FOUND"),
            Map.entry("Product is not active", "PRODUCT_IS_INACTIVE"),
            Map.entry("Already purchased", "PRODUCT_ALREADY_PURCHASED"),
            Map.entry("Product deckId is null", "BAD_PARAMETER"),
            Map.entry("Code not found for this phone number", "SMS_CODE_NOT_FOUND"),
            Map.entry("Code expired", "SMS_CODE_EXPIRED"),
            Map.entry("Invalid code", "SMS_CODE_IS_INVALID")
            );

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        String message = ex.getMessage() == null ? "Bad request" : ex.getMessage();
        String code = CODES.getOrDefault(message, "BAD_REQUEST");

        log.error("Request failed: {} {} -> {} ({})", req.getMethod(), req.getRequestURI(), message, code);

        return ResponseEntity.badRequest().body(new ErrorResponse(
                code,
                message,
                req.getRequestURI(),
                Instant.now()
        ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex, HttpServletRequest req) {
        // намеренно не светим детали
        log.error("Request failed: {} {} -> not found entity", req.getMethod(), req.getRequestURI());

        return ResponseEntity.badRequest().body(new ErrorResponse(
                "BAD_REQUEST",
                "Bad request",
                req.getRequestURI(),
                Instant.now()
        ));
    }
}