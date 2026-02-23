package org.example.datasource.repository;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.storage.GameStorage;
import org.example.domain.model.Game;

import java.util.UUID;

public class GameRepositoryImpl implements GameRepository {
    private final GameMapper mapper;
    private final GameStorage storage;

    public GameRepositoryImpl(GameMapper mapper, GameStorage storage) {
        this.mapper = mapper;
        this.storage = storage;
    }

    @Override
    public void save(Game game) {
        GameEntity entity = mapper.toEntity(game);
        if (entity == null)
            return;
        storage.save(entity);
    }

    @Override
    public Game findById(UUID gameId) {
        GameEntity entity = storage.findById(gameId);

        if (entity == null)
            return null;

        return mapper.toDomain(entity);
    }
}
