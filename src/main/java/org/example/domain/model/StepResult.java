package org.example.domain.model;

public class StepResult {
    private final Game game;
    private final String message;

    public StepResult(Game game, String message) {
        this.game = game;
        this.message = message;
    }

    public Game getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }
}
