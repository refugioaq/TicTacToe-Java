package org.example.web.controller;

import org.example.domain.model.*;
import org.example.domain.service.GameManagementService;
import org.example.domain.service.GameService;
import org.example.domain.service.UserService;
import org.example.web.mapper.WebGameMapper;
import org.example.web.model.GameDto;
import org.example.web.model.SignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameManagementService gameManagementService;
    private final WebGameMapper webGameMapper;

    public GameController(
            GameManagementService gameManagementService,
            GameService gameService,
            WebGameMapper webGameMapper,
            UserService userService
    ) {
        this.gameManagementService = gameManagementService;
        this.webGameMapper = webGameMapper;
    }

    @PostMapping("/start")
    public ResponseEntity<GameDto> startNewGame() {
        Game game = gameManagementService.startNewGame();
        return ResponseEntity.ok(webGameMapper.toDto(game, "Старт игры"));
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

        StepResult result = gameManagementService.gameProcess(gameId, requestField);

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

//    @PostMapping("/auth/register")
//    ResponseEntity<Void> register (@RequestBody @Validated SignUpRequest request) {
//        return (userService.register(request)) ?
//                ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
//    }
//
//    @PostMapping("/auth/login")
//    ResponseEntity<Void> logIn (@RequestBody @Validated SignUpRequest request) {
//        String credentials = request.getLogin() + ":" + request.getPassword();
//        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
//        if (userService.authorize(encoded) != null) return ResponseEntity.ok().build();
//        else return ResponseEntity.badRequest().build();
//    }
}