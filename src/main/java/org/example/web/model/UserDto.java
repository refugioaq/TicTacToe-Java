package org.example.web.model;

import org.example.domain.model.Role;

import java.util.UUID;

public record UserDto(UUID userId, String login, Role role) {
}
