package org.example.domain.service;

import org.example.domain.model.*;

public interface GameService {
    Move getNextMoveWithMinimax(Player player);

    boolean validateGameField(Game currentGameField, GameField newGameField);

    GameStatus checkGameEnd();

    void setField(Player[][] field);
}
