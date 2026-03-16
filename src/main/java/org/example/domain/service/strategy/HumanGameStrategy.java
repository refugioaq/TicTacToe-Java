package org.example.domain.service.strategy;

import org.example.datasource.mapper.GameMapper;
import org.example.datasource.repository.GameRepository;
import org.example.datasource.repository.UserRepository;
import org.example.domain.model.Game;
import org.example.domain.model.GameField;
import org.example.domain.model.StepResult;
import org.example.domain.service.gameService.GameService;

import java.util.UUID;

public class HumanGameStrategy extends AbstractGameStrategy {

    public HumanGameStrategy(GameService gameService, GameRepository gameRepository, GameMapper gameMapper, UserRepository userRepository) {
        super(gameService, gameRepository, gameMapper, userRepository);
    }

    @Override
    protected void validateTurn(Game game, UUID userId) {
        if (!userId.equals(game.getIdFirstPlayer()) && game.isTurnOfThePlayer())
            throw new IllegalStateException("Не ваш ход");

        if (!userId.equals(game.getIdSecondPlayer()) && !game.isTurnOfThePlayer())
            throw new IllegalStateException("Не ваш ход");
    }

    @Override
    public StepResult processMove(UUID gameId, GameField newField, UUID userId) {
        Game game = getStartValues(gameId);

        System.out.println(game.toString());

        validateMark(game, newField, userId);
        validateEndGame(game);
        validateTurn(game, userId);
        validateField(game, newField);

        game = applyMove(game, newField);
        game = handlePostMove(game);

        StepResult resultAfterPlayerMove = checkGameEnd(game, userId);
        saveGameWithResult(game, resultAfterPlayerMove, userId);

        return resultAfterPlayerMove;

    }

    private Game handlePostMove(Game game) {
        return game.withToggledTurn();
    }
}
