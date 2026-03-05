package org.example.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.domain.model.*;
import org.example.domain.service.GameManagementService;
import org.example.web.mapper.WebGameMapper;
import org.example.web.model.CreateGameRequest;
import org.example.web.model.GameDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameManagementService gameManagementService;
    private final WebGameMapper webGameMapper;

    public GameController(
            GameManagementService gameManagementService,
            WebGameMapper webGameMapper
    ) {
        this.gameManagementService = gameManagementService;
        this.webGameMapper = webGameMapper;
    }

    @PostMapping("/start")
    public ResponseEntity<GameDto> startNewGame(
            HttpServletRequest request,
            @RequestBody CreateGameRequest gameRequest
    ) {
        Game game = gameManagementService.startNewGame(webGameMapper.toDomainMode(gameRequest.mode()), (UUID) request.getAttribute("userId"));
        return ResponseEntity.ok(webGameMapper.toDto(game, "Старт игры"));
    }

    @PostMapping("/join/{gameId}")
    public ResponseEntity<GameDto> joinSecondPlayer (
            @PathVariable UUID gameId,
            HttpServletRequest request
    ) {
        Game game = gameManagementService.joinSecondPlayer(gameId, (UUID) request.getAttribute("userId"));
        return ResponseEntity.ok(webGameMapper.toDto(game, "Второй игрок присоединяется"));
    }

    @PostMapping("/{gameId}")
    ResponseEntity<GameDto> makeMove(
            @PathVariable UUID gameId,
            @RequestBody GameDto request,
            HttpServletRequest requestUserId
    ) {
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

        StepResult result = gameManagementService.gameProcess(gameId, requestField, (UUID) requestUserId.getAttribute("userId"));

        return ResponseEntity.ok(webGameMapper.toDto(
                result.getGame(),
                result.getMessage()
        ));
    }

    @GetMapping("/{gameId}")
    ResponseEntity<GameDto> getGame(@PathVariable UUID gameId) {
        StepResult result = gameManagementService.getStepResult(gameId);
        return ResponseEntity
                .ok(webGameMapper.toDto(
                        result.getGame(),
                        result.getMessage()
                ));
    }
}