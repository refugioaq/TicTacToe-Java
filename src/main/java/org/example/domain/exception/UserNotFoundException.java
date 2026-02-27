package org.example.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String login) {
        super("Пользователя с логином " + login + " не существует");
    }
}
