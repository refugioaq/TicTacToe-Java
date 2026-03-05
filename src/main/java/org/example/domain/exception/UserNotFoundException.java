package org.example.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String login) {
        super("Пользователя с логином " + login + " не существует");
    }

    public UserNotFoundException(UUID userId) {
        super("Пользователя с id " + userId + " не существует");
    }
}
