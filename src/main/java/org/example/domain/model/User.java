package org.example.domain.model;

import java.util.UUID;

public record User(UUID userId, String login, String password, Role role, String refreshToken) {
}
