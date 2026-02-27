package org.example.domain.service;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.repository.GameRepository;
import org.example.domain.exception.GameNotFoundException;
import org.example.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public record GameManagementService(
        GameRepository gameRepository,
        GameService gameService,
        GameMapper gameMapper
        ) {

    public Game startNewGame() {
        Player[][] emptyField = new Player[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                emptyField[i][j] = Player.EMPTY;
            }
        }

        GameField gameField = new GameField(emptyField);
        Game game = new Game(gameField);

        GameEntity entity = gameMapper.toEntity(game);
        gameRepository.save(entity);

        return game;
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
            case IN_PROGRESS -> "Игра продолжается";
        };

        return new StepResult(game, message);
    }

    public StepResult gameProcess(UUID gameId, GameField newField) {

        Game gameAfterPlayer = makeMove(gameId, newField);
        GameStatus statusAfterPlayer = gameService.checkGameEnd(gameAfterPlayer.getGameField().field());

        if (statusAfterPlayer == GameStatus.X_WON) {
            return new StepResult(gameAfterPlayer, "Игрок победил");
        } else if (statusAfterPlayer == GameStatus.DRAW) {
            return new StepResult(gameAfterPlayer, "Ничья");
        }

        Game gameAfterComputer = makeComputerMove(gameId);

        GameStatus statusAfterComputer = gameService.checkGameEnd(gameAfterComputer.getGameField().field());

        if (statusAfterComputer == GameStatus.O_WON) {
            return new StepResult(gameAfterComputer, "Компьютер победил");
        } else if (statusAfterComputer == GameStatus.DRAW) {
            return new StepResult(gameAfterComputer, "Ничья");
        }

        return new StepResult(gameAfterComputer, "Игра продолжается");
    }

    public Game makeMove(UUID gameId, GameField newField) {
        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        GameField field = gameMapper.toDomain(entity).getGameField();


        boolean isValid = gameService.validateGameField(field, newField);
        if (!isValid) {
            throw new IllegalArgumentException("Поле не изменено или изменено некорректно");
        }

        Player[][] result = deepCopyField(newField.field());
        GameField playerMoveField = new GameField(result);
        Game updatedGame = new Game(playerMoveField, gameId);
        GameEntity updatedGameEntity = gameMapper.toEntity(updatedGame);
        
        gameRepository.save(updatedGameEntity);
        return updatedGame;
    }

    public Game makeComputerMove(UUID gameId) {
        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        Game game = gameMapper.toDomain(entity);
        Move bestMove = gameService.getNextMoveWithMinimax(game.getGameField(), Player.O);

        GameField currentField = game.getGameField();
        Player[][] field = currentField.field();
        Player[][] newField = deepCopyField(field);
        newField[bestMove.getRow()][bestMove.getCol()] = Player.O;

        GameField computerMoveField = new GameField(newField);

        Game updatedGame = new Game(computerMoveField, gameId);

        gameRepository.save(gameMapper.toEntity(updatedGame));
        return updatedGame;
    }

    private Player[][] deepCopyField(Player[][] original) {
        Player[][] copy = new Player[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 3);
        }
        return copy;
    }
}