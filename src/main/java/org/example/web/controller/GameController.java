package org.example.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.domain.model.*;
import org.example.domain.security.JwtAuthentication;
import org.example.domain.service.GameManagementService;
import org.example.web.mapper.WebGameMapper;
import org.example.web.mapper.WebUserMapper;
import org.example.web.model.CreateGameRequest;
import org.example.web.model.GameDto;
import org.example.web.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameManagementService gameManagementService;
    private final WebGameMapper webGameMapper;
    private final WebUserMapper webUserMapper;

    public GameController(
            GameManagementService gameManagementService,
            WebGameMapper webGameMapper,
            WebUserMapper webUserMapper
    ) {
        this.gameManagementService = gameManagementService;
        this.webGameMapper = webGameMapper;
        this.webUserMapper = webUserMapper;
    }

    @PostMapping("/start")
    public ResponseEntity<GameDto> startNewGame(
            @RequestBody CreateGameRequest gameRequest
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal();

        Game game = gameManagementService.startNewGame(webGameMapper.toDomainMode(gameRequest.mode()), userId);
        return ResponseEntity.ok(webGameMapper.toDto(game, "Старт игры"));
    }

    @PostMapping("/join/{gameId}")
    public ResponseEntity<GameDto> joinSecondPlayer (
            @PathVariable UUID gameId,
            HttpServletRequest request
    ) {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UUID userId = authentication.getPrincipal();

        Game game = gameManagementService.joinSecondPlayer(gameId, userId);
        return ResponseEntity.ok(webGameMapper.toDto(game, "Второй игрок присоединяется"));
    }

    @PostMapping("/{gameId}")
    ResponseEntity<GameDto> makeMove(
            @PathVariable UUID gameId,
            @RequestBody GameDto request
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

        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UUID userId = authentication.getPrincipal();

        StepResult result = gameManagementService.gameProcess(gameId, requestField, userId);

        return ResponseEntity.ok(webGameMapper.toDto(
                result.getGame(),
                result.getMessage()
        ));
    }

    @GetMapping("/available")
    ResponseEntity<List<GameDto>> getAvailableGames() {
        List<Game> listGames = gameManagementService.getAvailableGames();
        List<GameDto> listGamesDto = listGames
                .stream()
                .map(game -> webGameMapper.toDto(game, "Игра доступна"))
                .toList();
        return ResponseEntity.ok(listGamesDto);
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

    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(webUserMapper.toDto(gameManagementService.getUserById(userId)));
    }
}