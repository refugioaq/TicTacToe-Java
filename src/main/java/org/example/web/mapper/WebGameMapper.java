package org.example.web.mapper;

import org.example.domain.model.Game;
import org.example.domain.model.Player;
import org.example.web.model.GameDto;

import java.util.UUID;

import static org.example.domain.Configurtion.SIZE;

public class WebGameMapper {
    public GameDto toDto(Game game, String message) {

        if (game == null || message == null) return null;
        return new GameDto(
                game.getGameId(),
                convertToWebField(game.getGameField().field()),
                message
        );
    }

    public GameDto toDtoError(UUID gameId, String[][] field, String message) {
        return new GameDto(gameId, field, message);
    }

    public Player[][] toDomain(String[][] field) {
        Player[][] domainField = new Player[3][3];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                domainField[i][j] = switch (field[i][j]) {
                    case "X" -> Player.X;
                    case "O" -> Player.O;
                    default -> Player.EMPTY;
                };
            }
        }
        return domainField;
    }

    public String[][] convertToWebField(Player[][] field) {
        String[][] playerField = new String[3][3];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                playerField[i][j] = switch (field[i][j]) {
                    case X -> "X";
                    case O -> "O";
                    default -> "EMPTY";
                };
            }
        }
        return playerField;
    }
}