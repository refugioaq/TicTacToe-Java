package org.example.domain.service.userService;

import org.example.domain.model.User;
import org.example.domain.security.JwtAuthentication;
import org.example.web.model.JwtRequest;
import org.example.web.model.JwtResponse;
import org.example.web.model.SignUpRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {
    @Transactional
    boolean register(SignUpRequest request);
    JwtResponse login(JwtRequest request);
    JwtResponse access(String refreshToken);
    JwtResponse refresh(String refreshToken);
    JwtAuthentication getJwtAuthentication();
    User getUserByAccessToken(UUID userId);
}
