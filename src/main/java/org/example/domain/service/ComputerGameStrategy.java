package org.example.domain.service;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.repository.GameRepository;
import org.example.domain.model.*;

import java.util.UUID;

public class ComputerGameStrategy extends AbstractGameStrategy{

    public ComputerGameStrategy(GameService gameService, GameRepository gameRepository, GameMapper gameMapper) {
        super(gameService, gameRepository, gameMapper);
    }

    @Override
    protected void validateTurn(Game game, UUID userId) {
        if (!userId.equals(game.getIdFirstPlayer())) {
            throw new IllegalStateException("Против компьютера ходит только создатель");
        }
    }

    @Override
    public StepResult processMove(UUID gameId, GameField newField, UUID userId) {
        Game game = getStartValues(gameId);

        validateTurn(game, userId);
        validateField(game, newField);

        game = applyMove(game, newField);

        StepResult resultAfterPlayerMove = checkGameEnd(game, userId);
        if (resultAfterPlayerMove.getStatus() != GameStatus.IN_PROGRESS) {
            saveGameWithResult(game, resultAfterPlayerMove, userId);
            return resultAfterPlayerMove;
        }

        Game gameAfterComputerMove = applyComputerMove(game, gameId);

        StepResult resultAfterComputerMove = checkGameEnd(gameAfterComputerMove, userId);
        saveGameWithResult(gameAfterComputerMove, resultAfterComputerMove, userId);

        return checkGameEnd(gameAfterComputerMove, userId);
    }

    public Game applyComputerMove(Game game, UUID gameId) {

        Move bestMove = gameService.getNextMoveWithMinimax(game.getGameField(), Player.O);

        GameField currentField = game.getGameField();
        Player[][] field = currentField.field();
        Player[][] newField = deepCopyField(field);
        newField[bestMove.getRow()][bestMove.getCol()] = Player.O;

        GameEntity entity = gameRepository.findById(gameId).orElseThrow();
        entity.setBoardState(gameMapper.convertArrayToString(newField));

        return gameMapper.toDomain(entity);
    }
}
