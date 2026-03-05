package org.example.domain.service;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.repository.GameRepository;
import org.example.domain.exception.GameNotFoundException;
import org.example.domain.model.*;

import java.util.UUID;

import static org.example.domain.Configuration.SIZE;

public abstract class AbstractGameStrategy implements GameModeStrategy {
    protected final GameService gameService;
    protected final GameRepository gameRepository;
    protected final GameMapper gameMapper;

    protected AbstractGameStrategy(GameService gameService, GameRepository gameRepository, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    protected abstract void validateTurn(Game game, UUID userId);

    protected Game getStartValues(UUID gameId) {
        GameEntity entity = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        return gameMapper.toDomain(entity);
    }

    protected void validateField(Game game, GameField newField) {
        boolean fieldValidator = gameService.validateGameField(game.getGameField(), newField);
        if (!fieldValidator)
            throw new IllegalArgumentException("Поле не изменено или изменено некорректно");
    }

    protected Game applyMove(Game game, GameField newField) {
        Player[][] field = deepCopyField(newField.field());
        GameEntity entity = gameMapper.toEntity(game);
        entity.setBoardState(gameMapper.convertArrayToString(field));

        return gameMapper.toDomain(entity);
    }

    protected StepResult checkGameEnd(Game game, UUID userId) {
        GameStatus status = gameService.checkGameEnd(game.getGameField().field());

        String message;

        if (status == GameStatus.X_WON) {
            message = "Игрок c id "+ userId + " победил";
        } else if (status == GameStatus.DRAW) {
            message = "Ничья";
        } else if (status == GameStatus.O_WON) {
            message = "Компьютер победил";
        } else {
            message = "Игра продолжается";
        }

        return new StepResult(game, status, userId, message);
    }


    protected void syncEntityWithResult(GameEntity entity, StepResult result, UUID userId) {
        if (result.getStatus() == GameStatus.X_WON) {
            entity.setWinner(userId);
        } else if (result.getStatus() == GameStatus.O_WON
                || result.getStatus() == GameStatus.DRAW) {
            entity.setWinner(null);
        }
        entity.setStatus(result.getStatus());
    }

    protected void saveGame(Game game) {
        gameRepository.save(gameMapper.toEntity(game));
    }

    protected void saveGameWithResult(Game game, StepResult result, UUID userId) {
        GameEntity entity = gameMapper.toEntity(game);
        syncEntityWithResult(entity, result, userId);
        gameRepository.save(entity);
    }

    protected Player[][] deepCopyField(Player[][] original) {
        Player[][] copy = new Player[SIZE][SIZE];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 3);
        }
        return copy;
    }
}
