package org.example.domain.service;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.exception.GameNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.model.*;

import java.util.Map;
import java.util.UUID;

public record GameManagementService(
        GameRepository gameRepository,
        UserRepository userRepository,
        GameService gameService,
        GameMapper gameMapper,
        Map<GameMode, GameModeStrategy> strategies
        ) {

    public Game startNewGame(GameMode mode, UUID userId) {
        Player[][] emptyField = new Player[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                emptyField[i][j] = Player.EMPTY;
            }
        }
        GameField gameField = new GameField(emptyField);
        Game game = new Game(gameField, userId, mode);

        GameEntity entity = gameMapper.toEntity(game);
        gameRepository.save(entity);

        return game;
    }

    public Game joinSecondPlayer(UUID gameId, UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (entity.getIdSecondPlayer() != null) {
            throw new IllegalStateException("Игра уже заполнена");
        }
        if (entity.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Игра уже завершена");
        }
        if (entity.getIdFirstPlayer().equals(userId)) {
            throw new IllegalArgumentException("Вы уже в игре");
        }

        entity.setIdSecondPlayer(userId);
        gameRepository.save(entity);

        return gameMapper.toDomain(entity);
    }

    public StepResult getStepResult(UUID gameId) {
        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        Game game = gameMapper.toDomain(entity);

        GameStatus status = gameService.checkGameEnd(game.getGameField().field());

        String message = switch (status) {
            case X_WON -> "Игрок победил";
            case DRAW -> "Ничья";
            case O_WON -> "Компьютер победил";
            case TURN_FIRST_PLAYER -> "Очередь игрока с айди: " + game.getIdFirstPlayer();
            case TURN_SECOND_PLAYER -> "Очередь игрока с айди: " + game.getIdSecondPlayer();
            case IN_PROGRESS -> "Игра продолжается";
        };
        // поправить айди
        return new StepResult(game, status, entity.getIdFirstPlayer(), message);
    }

    public StepResult gameProcess(UUID gameId, GameField field, UUID userId) {
        GameModeStrategy strategy = getStrategy(gameId);
        return strategy.processMove(gameId, field, userId);
    }

    private GameModeStrategy getStrategy(UUID gameId) {
        GameEntity entity = gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException(gameId));
        return strategies.get(entity.getMode());
    }
}