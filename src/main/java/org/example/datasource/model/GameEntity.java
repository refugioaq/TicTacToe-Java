package org.example.datasource.model;

import java.util.UUID;

public class GameEntity {
    private final GameFieldEntity gameFieldEntity;
    private final UUID gameId;

    public GameEntity(GameFieldEntity gameFieldEntity, UUID gameId) {
        this.gameFieldEntity = gameFieldEntity;
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameFieldEntity getGameFieldEntity() {
        return gameFieldEntity;
    }
}
