package org.example.domain.service.strategy;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.model.GameEntity;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.*;
import org.example.domain.service.gameService.GameService;

import java.util.UUID;

public class ComputerGameStrategy extends AbstractGameStrategy{

    public ComputerGameStrategy(GameService gameService, GameRepository gameRepository, GameMapper gameMapper, UserRepository userRepository) {
        super(gameService, gameRepository, gameMapper, userRepository);
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

        validateMark(game, newField, userId);
        validateEndGame(game);
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

        return resultAfterComputerMove;
    }

    public Game applyComputerMove(Game game, UUID gameId) {

        Move bestMove = gameService.getNextMoveWithMinimax(game.getGameField(), Player.O);

        GameField currentField = game.getGameField();
        Player[][] field = currentField.field();
        Player[][] newField = deepCopyField(field);
        newField[bestMove.getRow()][bestMove.getCol()] = Player.O;

        GameEntity entity = gameRepository.findById(gameId).orElseThrow();
        entity.setBoardState(gameMapper.convertArrayToString(newField));

        gameRepository.save(entity);

        return gameMapper.toDomain(entity);
    }
}
