package org.example.datasource.storage;

import org.example.datasource.model.GameEntity;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameStorage {
    private final ConcurrentHashMap<UUID, GameEntity> storage =
            new ConcurrentHashMap<>();

    public void save(GameEntity entity) {
        storage.put(entity.getGameId(), entity);
    }
    public GameEntity findById(UUID gameId) {
        return storage.get(gameId);
    }
}
