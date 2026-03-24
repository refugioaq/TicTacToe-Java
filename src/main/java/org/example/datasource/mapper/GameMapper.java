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
                getStrinGameMode(game.getMode()));
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
                String.valueOf(entity.getCreatedAt()));
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

    private String getStrinGameMode(GameMode mode) {
        return mode == GameMode.HUMAN ? "HUMAN" : "COMPUTER";
    }

    public String getStringGameStatus(Game game) {
        return switch (game.getStatus()) {
            case IN_PROGRESS -> "Игра продолжается";
            case O_WON -> (game.getMode() == GameMode.HUMAN) ? "Победа игрока" : "Победа компьютера";
            case X_WON -> "Победа игрока";
            case DRAW -> "Ничья";
        };
    }
    
    private GameStatus getGameStatus(GameEntity entity) {
        UUID winnerId = entity.getWinner();
        
        return switch (entity.getStatus()) {
            case "Игра продолжается" -> GameStatus.IN_PROGRESS;
            case "Победа игрока" -> (entity.getMode().equals("COMPUTER")) ?  GameStatus.X_WON :
                    winnerId.equals(entity.getIdFirstPlayer()) ? GameStatus.X_WON : GameStatus.O_WON ;
            case "Ничья" -> GameStatus.DRAW;
            case "Победа компьютера" -> GameStatus.O_WON;
            default -> throw new IllegalStateException("Unexpected value: " + entity.getStatus());
        };
    }
    
    public GameMode getGameMode(String mode) {
        return mode.equals("HUMAN") ? GameMode.HUMAN : GameMode.COMPUTER;
    }
}
