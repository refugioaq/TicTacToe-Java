package org.example.web.controller;

import org.example.domain.service.UserService;
import org.example.web.model.SignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody @Validated SignUpRequest request) {
        return (userService.register(request)) ?
                ResponseEntity.ok("Регистрация прошла успешно") :
                ResponseEntity.badRequest().body("Регистрация провалена");
    }

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody @Validated SignUpRequest request) {
        String credentials = request.getLogin() + ":" + request.getPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        if (userService.authorize(encoded) != null) return ResponseEntity.ok("Авторизация прошла успешно");
        else return ResponseEntity.badRequest().body("Авторизация провалена");
    }
}