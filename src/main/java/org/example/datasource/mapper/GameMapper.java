package org.example.datasource.mapper;

import org.example.datasource.model.GameEntity;
import org.example.domain.model.Game;
import org.example.domain.model.GameField;
import org.example.domain.model.Player;

import static org.example.domain.Configurtion.SIZE;

public class GameMapper {
    public GameEntity toEntity(Game game) {
        if (game == null) return null;


        String boardState = convertArrayToString(game.getGameField().field());

        return new GameEntity(boardState, game.getGameId());
    }

    public Game toDomain(GameEntity entity) {
        if (entity == null) return null;

        Player[][] field = convertStringToArray(entity.getBoardState());
        GameField gameField = new GameField(field);

        return new Game(gameField, entity.getGameId());
    }

    private String convertArrayToString(Player[][] field) {
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
}
