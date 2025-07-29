package com.pragma.plazoleta.infrastructure.exception.handler;

import com.pragma.plazoleta.domain.exception.DomainException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String MESSAGE_KEY = "message";

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(MESSAGE_KEY, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "Validation error";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE_KEY, message));
    }

    @ExceptionHandler({MalformedJwtException.class, JwtException.class})
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        String message = "Error en el token JWT: " + ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(MESSAGE_KEY, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(MESSAGE_KEY, ex.getMessage()));
    }
} 