package org.example.web.controller;

import org.example.domain.security.JwtAuthentication;
import org.example.domain.service.security.JwtProvider;
import org.example.domain.service.userService.UserService;
import org.example.web.mapper.WebUserMapper;
import org.example.web.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;
    private final WebUserMapper mapper;

    public UserController(UserService userService, WebUserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    // запрос - login + password
    // ответ - мессэдж
    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody SignUpRequest request) {
        return (userService.register(request)) ?
                ResponseEntity.ok("Регистрация прошла успешно") :
                ResponseEntity.badRequest().body("Регистрация провалена");
    }

    // запрос - login + password
    // ответ - json (type, access, refresh тот же)
    @PostMapping("/login")
    ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // запрос - refresh token
    // ответ - json (type, access, refresh) (или ошибка refresh устарел)
    @GetMapping("/refresh/access")
    ResponseEntity<JwtResponse> access(@RequestBody RefreshJwtRequest request) {
        return ResponseEntity.ok(userService.access(request.refreshToken()));
    }

    // запрос - refresh token
    // ответ - json (type, access, refresh - другой)
    @GetMapping("/refresh/refresh")
    ResponseEntity<JwtResponse> refresh(@RequestBody RefreshJwtRequest request) {
        return ResponseEntity.ok(userService.refresh(request.refreshToken()));
    }

    @GetMapping("/me")
    ResponseEntity<UserDto> getUserData() {
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UUID userId = authentication.getPrincipal();

        return ResponseEntity.ok(mapper.toDto(userService.getUserByAccessToken(userId)));
    }
}