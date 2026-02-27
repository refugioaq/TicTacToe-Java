package org.example.datasource.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {
    @Column(name = "field", nullable = false)
    private String boardState;
    @Id
    private UUID gameId;

    public GameEntity() {}

    public GameEntity(String boardState, UUID gameId) {
        this.boardState = boardState;
        this.gameId = gameId;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}
