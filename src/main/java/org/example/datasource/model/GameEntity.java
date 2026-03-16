package org.example.datasource.model;

import jakarta.persistence.*;
import org.example.domain.model.GameMode;
import org.example.domain.model.GameStatus;

import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    private UUID gameId;

    @Column(name = "field", nullable = false)
    private String boardState;

    @Column(name = "first_player")
    private UUID idFirstPlayer;

    @Column(name = "second_player")
    private UUID idSecondPlayer;

    @Column
    private UUID winner;

    @Column
    private boolean turnOfThePlayer;

    @Column
    private String status;

    private String mode;

    public GameEntity() {
    }

    public GameEntity(UUID gameId, String boardState, UUID idFirstPlayer, UUID idSecondPlayer, UUID winner, boolean turnOfThePlayer, String status, String mode) {
        this.gameId = gameId;
        this.boardState = boardState;
        this.idFirstPlayer = idFirstPlayer;
        this.idSecondPlayer = idSecondPlayer;
        this.winner = winner;
        this.turnOfThePlayer = turnOfThePlayer;
        this.status = status;
        this.mode = mode;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public UUID getIdFirstPlayer() {
        return idFirstPlayer;
    }

    public void setIdFirstPlayer(UUID idFirstPlayer) {
        this.idFirstPlayer = idFirstPlayer;
    }

    public UUID getIdSecondPlayer() {
        return idSecondPlayer;
    }

    public void setIdSecondPlayer(UUID idSecondPlayer) {
        this.idSecondPlayer = idSecondPlayer;
    }

    public UUID getWinner() {
        return winner;
    }

    public void setWinner(UUID winner) {
        this.winner = winner;
    }

    public boolean isTurnOfThePlayer() {
        return turnOfThePlayer;
    }

    public void setTurnOfThePlayer(boolean turnOfThePlayer) {
        this.turnOfThePlayer = turnOfThePlayer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}