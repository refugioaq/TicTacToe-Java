package org.example.domain.model;

import java.util.UUID;

public class StepResult {
    private final Game game;
    private final GameStatus status;
    private final UUID newPlayerId;
    private final String message;

    public StepResult(Game game, GameStatus status, UUID newPlayerId, String message) {
        this.game = game;
        this.status = status;
        this.newPlayerId = newPlayerId;
        this.message = message;
    }

    public Game getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    public GameStatus getStatus() {
        return status;
    }

    public UUID getNewPlayerId() {
        return newPlayerId;
    }
}
