package org.example.domain.service;

import org.example.datasource.repository.GameRepository;
import org.example.domain.model.*;
import java.util.UUID;

public record GameManagementService(
        GameRepository gameRepository,
        GameServiceImpl gameServiceImpl
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
        gameRepository.save(game);
        return game;
    }

    public Game getGame(UUID gameId) {
        if (gameId == null) return null;
        return gameRepository.findById(gameId);
    }

    public Game makeMove(UUID gameId, GameField newField) {
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Игра не найдена");
        }

        boolean isValid = gameServiceImpl.validateGameField(game, newField);
        if (!isValid) {
            throw new IllegalArgumentException("Есть косяк");
        }

        Player[][] result = deepCopyField(newField.field());

        GameField playerMoveField = new GameField(result);
        Game updatedGame = new Game(playerMoveField, gameId);

        gameRepository.save(updatedGame);
        return updatedGame;
    }

    public Game makeComputerMove(UUID gameId) {
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Игра не найдена");
        }

        Move bestMove = gameServiceImpl.findBestMove(game.getGameField(), Player.O);

        GameField currentField = game.getGameField();
        Player[][] field = currentField.field();
        Player[][] newField = deepCopyField(field);
        newField[bestMove.getRow()][bestMove.getCol()] = Player.O;

        GameField computerMoveField = new GameField(newField);
        Game updatedGame = new Game(computerMoveField, gameId);

        gameRepository.save(updatedGame);
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