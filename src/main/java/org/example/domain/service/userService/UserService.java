package org.example.domain.service.userService;

import org.example.web.model.SignUpRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {
    @Transactional
    boolean register(SignUpRequest request);
    UUID authorize(String base64);
}
