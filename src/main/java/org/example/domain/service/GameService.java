package org.example.domain.service;

import org.example.domain.model.*;

public interface GameService {
    Move getNextMoveWithMinimax(GameField field, Player player);

    boolean validateGameField(GameField currentGameField, GameField newGameField);

    GameStatus checkGameEnd(Player[][] field);
}
