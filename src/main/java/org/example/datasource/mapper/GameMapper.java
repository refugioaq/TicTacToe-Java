package org.example.datasource.mapper;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.*;

import java.util.UUID;

import static org.example.domain.Configuration.SIZE;

public class GameMapper {
    public GameEntity toEntity(Game game) {
        if (game == null) return null;
        String boardState = convertArrayToString(game.getGameField().field());

        return new GameEntity(game.getGameId(),
                boardState,
                game.getIdFirstPlayer(),
                game.getIdSecondPlayer(),
                game.getWinner(),
                game.isTurnOfThePlayer(),
                getStringGameStatus(game),
                getStringGameMode(game.getMode()),
                game.getCreatedAt());
    }

    public Game toDomain(GameEntity entity) {
        if (entity == null) return null;

        Player[][] field = convertStringToArray(entity.getBoardState());
        GameField gameField = new GameField(field);

        return new Game(gameField,
                entity.getGameId(),
                entity.getIdFirstPlayer(),
                entity.getIdSecondPlayer(),
                entity.getWinner(),
                entity.isTurnOfThePlayer(),
                getGameStatus(entity),
                getGameMode(entity.getMode()),
                entity.getCreatedAt());
    }

    public String convertArrayToString(Player[][] field) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (field[i][j]) {
                    case X -> sb.append("X");
                    case O -> sb.append("O");
                    case EMPTY -> sb.append("_");
                }
            }
        }
        return sb.toString();
    }

    private Player[][] convertStringToArray(String boardState) {
        Player[][] field = new Player[SIZE][SIZE];

        for (int i = 0; i < boardState.length(); i++) {
            field[i / SIZE][i % SIZE] = switch (boardState.charAt(i)) {
                case 'X' -> Player.X;
                case 'O' -> Player.O;
                default -> Player.EMPTY;
            };
        }
        return field;
    }

    private String getStringGameMode(GameMode mode) {
        return mode == GameMode.HUMAN ? "HUMAN" : "COMPUTER";
    }

    public String getStringGameStatus(Game game) {
        return String.valueOf(game.getStatus());
    }
    
    private GameStatus getGameStatus(GameEntity entity) {
        return GameStatus.valueOf(entity.getStatus());
    }
    
    public GameMode getGameMode(String mode) {
        return mode.equals("HUMAN") ? GameMode.HUMAN : GameMode.COMPUTER;
    }
}
