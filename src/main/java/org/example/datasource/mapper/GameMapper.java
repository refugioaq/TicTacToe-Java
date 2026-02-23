package org.example.datasource.mapper;

import org.example.datasource.model.GameFieldEntity;
import org.example.datasource.model.GameEntity;
import org.example.domain.model.GameField;
import org.example.domain.model.Game;

public class GameMapper {
    public GameEntity toEntity(Game game) {
        if (game == null) return null;

        GameFieldEntity gameFieldEntity = new GameFieldEntity(game.getGameField().field());

        return new GameEntity(gameFieldEntity, game.getGameId());
    }

    public Game toDomain(GameEntity entity) {
        if (entity == null) return null;

        GameField gameField = new GameField(entity.getGameFieldEntity().field());
        return new Game(gameField, entity.getGameId());
    }
}
