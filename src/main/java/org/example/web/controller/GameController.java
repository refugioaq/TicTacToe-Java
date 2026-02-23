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

            Player[][] playerField = webGameMapper.toDomain(request.field());
            GameField requestField = new GameField(playerField);

            StepResult result = gameManagementService.gameProcess(gameId, requestField);

            return ResponseEntity.ok(webGameMapper.toDto(
                    result.getGame(),
                    result.getMessage()
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
            StepResult result = gameManagementService.getStepResult(gameId);

            return ResponseEntity
                    .ok(webGameMapper.toDto(
                            result.getGame(),
                            result.getMessage()
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