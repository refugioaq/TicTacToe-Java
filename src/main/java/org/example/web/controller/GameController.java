package org.example.web.controller;

import org.example.domain.model.*;
import org.example.domain.service.GameManagementService;
import org.example.domain.service.GameService;
import org.example.web.mapper.WebGameMapper;
import org.example.web.model.GameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameManagementService gameManagementService;
    private final GameService gameService;
    private final WebGameMapper webGameMapper;

    public GameController(
            GameManagementService gameManagementService,
            GameService gameService,
            WebGameMapper webGameMapper
    ) {
        this.gameManagementService = gameManagementService;
        this.gameService = gameService;
        this.webGameMapper = webGameMapper;
    }

    @PostMapping("/start")
    public ResponseEntity<GameDto> startNewGame() {
        try {
            Game game = gameManagementService.startNewGame();

            return ResponseEntity.ok(webGameMapper.toDto(game, "Старт игры"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(webGameMapper.toDto(null, "Ошибка при создание игры" + e.getMessage()));
        }
    }

    @PostMapping("/{gameId}")
    ResponseEntity<GameDto> makeMove(
            @PathVariable UUID gameId,
            @RequestBody GameDto request
    ) {
        try {
            if (!gameId.equals(request.gameId())) {
                return ResponseEntity
                        .badRequest()
                        .body(webGameMapper.toDtoError(gameId,
                                request.field(),
                                "ID игры не совпадают"
                        ));
            }

            Game currentGame = gameManagementService.getGame(gameId);
            if (currentGame == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(webGameMapper.toDtoError(gameId,
                                request.field(),
                                "Игра не найдена"
                        ));
            }

            Player[][] playerField = webGameMapper.toDomain(request.field());
            GameField requestField = new GameField(playerField);

            boolean isValid = gameService.validateGameField(currentGame, requestField);
            if (!isValid) {
                return ResponseEntity
                        .badRequest()
                        .body(webGameMapper.toDto(
                                currentGame,
                                "Некорректный ход: поле изменено не по правилам или не изменено"
                        ));
            }
            Game gameAfterPlayer = gameManagementService.makeMove(gameId, requestField);

            gameService.setField(gameAfterPlayer.getGameField().field());

            GameStatus status = gameService.checkGameEnd();

            if (status == GameStatus.X_WON) {
                return ResponseEntity.ok(webGameMapper.toDto(
                        gameAfterPlayer,
                        "Игрок победил"
                        ));
            } else if (status == GameStatus.DRAW) {
                return ResponseEntity.ok(webGameMapper.toDto(
                        gameAfterPlayer,
                        "Ничья"
                ));
            }

            Game gameAfterComputer = gameManagementService.makeComputerMove(gameId);

            gameService.setField(gameAfterComputer.getGameField().field());

            GameStatus finalState = gameService.checkGameEnd();

            String message = switch (finalState) {
                case O_WON -> "Компьютер победил";
                case DRAW -> "Ничья";
                default -> "Игра продолжается";
            };

            return ResponseEntity.ok(webGameMapper.toDto(
                    gameAfterComputer,
                    message
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(webGameMapper.toDtoError(
                            gameId,
                            null,
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(webGameMapper.toDtoError(
                            gameId,
                            null,
                            "Ошибка при обработке: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/{gameId}")
    ResponseEntity<GameDto> getGame(@PathVariable UUID gameId) {
        try {
            Game game = gameManagementService.getGame(gameId);

            if (game == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(webGameMapper.toDtoError(
                                gameId,
                                null,
                                "Игра не найдена"
                        ));
            }
            GameStatus status = gameService.checkGameEnd();

            String message = switch (status) {
                case X_WON -> "Игрок победил";
                case DRAW -> "Ничья";
                case O_WON -> "Компьютер победил";
                case IN_PROGRESS -> "Игра продолжается";
            };

            return ResponseEntity
                    .ok(webGameMapper.toDto(
                            game,
                            message
                    ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(webGameMapper.toDtoError(
                            gameId,
                            null,
                            "Ошибка при получении игры123" +
                                    ": " + e.getMessage()
                    ));
        }
    }
}