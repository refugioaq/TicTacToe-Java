package org.example.domain.service;

import org.example.web.model.SignUpRequest;

import java.util.UUID;

public interface UserService {
    boolean register(SignUpRequest request);
    UUID authorize(String base64);
}
