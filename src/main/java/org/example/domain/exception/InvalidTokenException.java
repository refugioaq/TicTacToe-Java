package org.example.domain.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Токен невалиден или устарел");
    }
}
