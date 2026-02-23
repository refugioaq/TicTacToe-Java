package org.example.domain.service;


import org.example.domain.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.example.domain.Configurtion.SIZE;

public class GameServiceImpl implements GameService {
    private Player[][] field = new Player[3][3];

    public Move findBestMove(GameField field, Player player) {
        GameField copy = field.copy();
        this.field = copy.field();
        return getNextMoveWithMinimax(player);
    }

    @Override
    public Move getNextMoveWithMinimax(Player player) {
        int bestScore = Integer.MAX_VALUE;
        Move bestMove = null;
        List<Move> moves = getAvailableMoves();
        for (Move move : moves) {
            field[move.getRow()][move.getCol()] = player;

            int score = minimax(0, true);
            field[move.getRow()][move.getCol()] = Player.EMPTY;

            if (score < bestScore) {
                bestScore = score;
                bestMove = move;
            }

        }
        return bestMove;

    }

    @Override
    public boolean validateGameField(Game currentGameField, GameField newGameField) {
        Player[][] oldField = currentGameField.getGameField().field();
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
    public GameStatus checkGameEnd() {

        if(checkWin(Player.X)) return GameStatus.X_WON;

        if (checkWin(Player.O)) return GameStatus.O_WON;

        if (!hasEmptyCeil()) return GameStatus.DRAW;

        return GameStatus.IN_PROGRESS;
    }

    @Override
    public void setField(Player[][] field) {
        this.field = field;
    }

    public int evaluateBoard(GameStatus status, int depth) {
        if (status == GameStatus.X_WON) {
            return 1000 - depth;
        } else if (status == GameStatus.O_WON) {
            return depth - 1000;
        }
        return 0;
    }

    public int minimax(int depth, boolean isMaximizing) {
//        System.out.println("minimax: depth=" + depth + ", isMax=" + isMaximizing);
        GameStatus status = checkGameEnd();
        if (status != GameStatus.IN_PROGRESS) {
            return evaluateBoard(status, depth);
        }

        int bestScore;
        if(isMaximizing) {
            bestScore = Integer.MIN_VALUE;

            for (Move move : getAvailableMoves()) {
                field[move.getRow()][move.getCol()] = Player.X;
                int score = minimax(depth + 1, false);
                field[move.getRow()][move.getCol()] = Player.EMPTY;

                bestScore = Math.max(bestScore, score);
            }

        } else {
            bestScore = Integer.MAX_VALUE;

            List<Move> moves = getAvailableMoves();

            for (Move move : moves) {
                field[move.getRow()][move.getCol()] = Player.O;
                int score = minimax(depth + 1, true);
                field[move.getRow()][move.getCol()] = Player.EMPTY;

                bestScore = Math.min(bestScore, score);
            }

        }
        return bestScore;
    }

    public boolean checkWin(Player player) {
        for (int i = 0; i < SIZE; i++) {
            if (field[i][0] == player && field[i][1] == player && field[i][2] == player)
                return true;

            if (field[0][i] == player && field[1][i] == player && field[2][i] == player)
                return true;
        }

        return (field[0][0] == player && field[1][1] == player && field[2][2] == player)
                || (field[0][2] == player && field[1][1] == player && field[2][0] == player);
    }

    private boolean hasEmptyCeil() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j] == Player.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Move> getAvailableMoves() {
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
