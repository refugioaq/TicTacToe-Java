package org.example.domain.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super("Игра с ID: " + gameId + " не найдена");
    }
}
