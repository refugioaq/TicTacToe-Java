package org.example.web.controller;

import org.example.domain.exception.GameNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<String> handleGameNotFound(GameNotFoundException  e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(GameNotFoundException  e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadReques(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception e) {
        return ResponseEntity.status(500).body("Internal error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }
}
