package org.example.domain.service;


import org.example.domain.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.example.domain.Configurtion.SIZE;

public class GameServiceImpl implements GameService {
    @Override
    public Move getNextMoveWithMinimax(GameField copy, Player player) {
        int bestScore = Integer.MAX_VALUE;
        Move bestMove = null;
        Player[][] field = copy.field();
        List<Move> moves = getAvailableMoves(field);
        for (Move move : moves) {
            field[move.getRow()][move.getCol()] = player;

            int score = minimax(field, 0, true);
            field[move.getRow()][move.getCol()] = Player.EMPTY;

            if (score < bestScore) {
                bestScore = score;
                bestMove = move;
            }

        }
        return bestMove;

    }

    @Override
    public boolean validateGameField(GameField currentGameField, GameField newGameField) {
        Player[][] oldField = currentGameField.field();
        Player[][] newField = newGameField.field();
        int newState = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (oldField[i][j] != newField[i][j]) {
                    newState++;
                    if (oldField[i][j] != Player.EMPTY || newState > 1) return false;
                }
            }
        }
        return newState != 0;
    }

    @Override
    public GameStatus checkGameEnd(Player[][] field) {

        if(checkWin(field, Player.X)) return GameStatus.X_WON;

        if (checkWin(field, Player.O)) return GameStatus.O_WON;

        if (!hasEmptyCeil(field)) return GameStatus.DRAW;

        return GameStatus.IN_PROGRESS;
    }

    public int evaluateBoard(GameStatus status, int depth) {
        if (status == GameStatus.X_WON) {
            return 1000 - depth;
        } else if (status == GameStatus.O_WON) {
            return depth - 1000;
        }
        return 0;
    }

    public int minimax(Player[][] field, int depth, boolean isMaximizing) {
//        System.out.println("minimax: depth=" + depth + ", isMax=" + isMaximizing);
        GameStatus status = checkGameEnd(field);
        if (status != GameStatus.IN_PROGRESS) {
            return evaluateBoard(status, depth);
        }

        int bestScore;
        if(isMaximizing) {
            bestScore = Integer.MIN_VALUE;

            for (Move move : getAvailableMoves(field)) {
                field[move.getRow()][move.getCol()] = Player.X;
                int score = minimax(field, depth + 1, false);
                field[move.getRow()][move.getCol()] = Player.EMPTY;

                bestScore = Math.max(bestScore, score);
            }

        } else {
            bestScore = Integer.MAX_VALUE;

            List<Move> moves = getAvailableMoves(field);

            for (Move move : moves) {
                field[move.getRow()][move.getCol()] = Player.O;
                int score = minimax(field, depth + 1, true);
                field[move.getRow()][move.getCol()] = Player.EMPTY;

                bestScore = Math.min(bestScore, score);
            }

        }
        return bestScore;
    }

    public boolean checkWin(Player[][] field, Player player) {
        for (int i = 0; i < SIZE; i++) {
            if (field[i][0] == player && field[i][1] == player && field[i][2] == player)
                return true;

            if (field[0][i] == player && field[1][i] == player && field[2][i] == player)
                return true;
        }

        return (field[0][0] == player && field[1][1] == player && field[2][2] == player)
                || (field[0][2] == player && field[1][1] == player && field[2][0] == player);
    }

    private boolean hasEmptyCeil(Player[][] field) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] == Player.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Move> getAvailableMoves(Player[][] field) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] == Player.EMPTY) {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    }
}
