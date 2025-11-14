package com.gatepass.backend.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuth(AuthenticationException exception) {
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception exception) {
        return ResponseEntity.status(500).body(Map.of("error", "ServerError", "message", exception.getMessage()));
    } 
}
