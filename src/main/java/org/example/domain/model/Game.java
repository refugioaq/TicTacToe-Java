package org.example.domain.model;

import java.util.UUID;

public class Game {
    private final GameField gameField;
    private final UUID gameId;

    public Game(GameField gameField) {
        this.gameField = gameField;
        this.gameId = UUID.randomUUID();
    }

    public Game(GameField gameField, UUID uuid) {
        this.gameField = gameField;
        this.gameId = uuid;
    }

    public GameField getGameField() {
        return gameField;
    }

    public UUID getGameId() {
        return gameId;
    }
}
