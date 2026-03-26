package org.example.domain.service.strategy;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.model.UserEntity;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.exception.GameNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.model.*;
import org.example.domain.service.gameService.GameService;

import java.util.UUID;

import static org.example.domain.Configuration.SIZE;

public abstract class AbstractGameStrategy implements GameModeStrategy {
    protected final GameService gameService;
    protected final GameRepository gameRepository;
    protected final GameMapper gameMapper;
    protected final UserRepository userRepository;

    protected AbstractGameStrategy(GameService gameService, GameRepository gameRepository, GameMapper gameMapper, UserRepository userRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.userRepository = userRepository;

    }

    protected abstract void validateTurn(Game game, UUID userId);

    protected void validateMark(Game game, GameField newField, UUID userId) {
        char playerMark = game.getPlayerMark(userId);
        Player playerEnum = (playerMark == 'X') ? Player.X : Player.O;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (newField.field()[i][j] != game.getGameField().field()[i][j]) {
                    if (newField.field()[i][j] != playerEnum) {
                        throw new IllegalArgumentException("Вы не можете ставить " + newField.field()[i][j]);
                    }
                }
            }
        }
    }

    protected void validateEndGame(Game game) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Игра завершена");
        }
    }

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
        String login = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)).getLogin();

        String message;

        if ((status == GameStatus.X_WON || status == GameStatus.O_WON) && game.getMode() == GameMode.HUMAN) {
            message = "Игрок c логином "+ login + " победил";
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
        if (result.getStatus() == GameStatus.X_WON ||
                (result.getStatus() == GameStatus.O_WON && entity.getMode().equals("HUMAN"))) {
            entity.setWinner(userId);
        } else if (result.getStatus() == GameStatus.DRAW) {
            entity.setWinner(null);
        }

        String status = getStringGameStatusFromEnum(result.getStatus());
        entity.setStatus(status);
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

    private String getStringGameStatusFromEnum(GameStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "IN_PROGRESS";
            case X_WON -> "X_WON";
            case O_WON -> "O_WON";
            case DRAW -> "DRAW";
        };
    }
}
