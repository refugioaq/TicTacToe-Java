package org.example.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) { super("Пользователя с id " + userId + " не существует");}
    public UserNotFoundException(String login) { super("Пользователя с логином " + login + " не существует");}
    public UserNotFoundException() { super("Логин или пароль неверен");}
}
