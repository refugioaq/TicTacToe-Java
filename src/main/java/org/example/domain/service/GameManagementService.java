package org.example.domain.service;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.mapper.UserMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.model.UserEntity;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.exception.GameNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.model.*;
import org.example.domain.service.gameService.GameService;
import org.example.domain.service.strategy.GameModeStrategy;
import org.example.web.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

public record GameManagementService(
        GameRepository gameRepository,
        UserRepository userRepository,
        GameService gameService,
        GameMapper gameMapper,
        UserMapper userMapper,
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

        if (entity.getMode().equals("COMPUTER")) {
            throw new IllegalStateException("Игра против компьютера, второк игрок не нужен");
        }
        if (entity.getIdSecondPlayer() != null) {
            throw new IllegalStateException("Игра уже заполнена");
        }
        if (!entity.getStatus().equals("IN_PROGRESS")) {
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
            case X_WON -> "Победил игрок с ником " +
                    userRepository.findById(game.getIdFirstPlayer())
                            .orElseThrow(null).getLogin();
            case DRAW -> "Ничья";
            case O_WON -> game.getMode() == GameMode.HUMAN ?
                    "Победил игрок с ником " + userRepository.findById(game.getIdSecondPlayer())
                            .orElseThrow(null).getLogin() : "Компьютер победил";
            case IN_PROGRESS -> "Игра продолжается";
        };

        UUID currentId =  entity.isTurnOfThePlayer()
                ? entity.getIdFirstPlayer()
                : entity.getIdSecondPlayer();

        return new StepResult(game, status, currentId, message);
    }

    public StepResult gameProcess(UUID gameId, GameField field, UUID userId) {
        GameModeStrategy strategy = getStrategy(gameId);
        return strategy.processMove(gameId, field, userId);
    }

    public List<Game> getAvailableGames() {
        return StreamSupport.stream(
                gameRepository.findAll().spliterator(),
                false
        )
                .filter(entity -> entity.getStatus().equals("IN_PROGRESS"))
                .filter(entity -> entity.getMode().equals("HUMAN"))
                .filter(entity -> entity.getIdSecondPlayer() == null)
                .map(gameMapper::toDomain)
                .toList();
    }

    public List<Game> getGamesByUser(UUID userId) {
        return gameRepository
                .findCompletedGamesByUserId(userId)
                .stream()
                .map(gameMapper::toDomain)
                .toList();
    }

    public  List<LeaderboardEntry> getTopPlayers(int top) {
        List<Object[]> leaderboardObjects = gameRepository.getTop(top);

        return leaderboardObjects
                .stream()
                .map(row -> new LeaderboardEntry(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2]
                ))
                .toList();
    }

    public User getUserById(UUID userId) {
        UserEntity entity = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        return userMapper.toDomain(entity);
    }

    private GameModeStrategy getStrategy(UUID gameId) {
        GameEntity entity = gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException(gameId));
        return strategies.get(gameMapper.getGameMode(entity.getMode()));
    }
}