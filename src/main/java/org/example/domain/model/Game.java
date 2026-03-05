package org.example.domain.model;

import java.util.UUID;

public class Game {
    private final GameField gameField;
    private final UUID gameId;
    private final UUID idFirstPlayer;
    private final UUID idSecondPlayer;
    private final UUID winner;
    private final boolean turnOfThePlayer;
    private final GameStatus status;
    private final GameMode mode;

    public Game(GameField gameField,
                UUID idFirstPlayer, GameMode mode
                ) {
        this.gameId = UUID.randomUUID();
        this.gameField = gameField;
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = null;
        this.winner = null;
        this.turnOfThePlayer = true;
        status = GameStatus.IN_PROGRESS;
        this.mode = mode;
    }

    public Game(GameField gameField,
                UUID idFirstPlayer
    ) {
        this.gameId = UUID.randomUUID();
        this.gameField = gameField;
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = null;
        this.winner = null;
        this.turnOfThePlayer = true;
        status = GameStatus.IN_PROGRESS;
        this.mode = GameMode.COMPUTER;
    }
    public Game(GameField gameField, UUID uuid, UUID idFirstPlayer, UUID idSecondPlayer, UUID winner, boolean turnOfThePlayer, GameStatus status, GameMode mode) {
        this.gameField = gameField;
        this.gameId = uuid;
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = idSecondPlayer;
        this.winner = winner;
        this.turnOfThePlayer = turnOfThePlayer;
        this.status = status;
        this.mode = mode;
    }

    public GameField getGameField() {
        return gameField;
    }

    public UUID getIdFirstPlayer() {
        return idFirstPlayer;
    }

    public UUID getIdSecondPlayer() {
        return idSecondPlayer;
    }

    public UUID getWinner() {
        return winner;
    }

    public boolean isTurnOfThePlayer() {
        return turnOfThePlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameMode getMode() {
        return mode;
    }

    public UUID getGameId() {
        return gameId;
    }

    public Game withToggledTurn() {
        return new Game(gameField, gameId, idFirstPlayer, idSecondPlayer, winner, !turnOfThePlayer, status, mode);
    }
}
