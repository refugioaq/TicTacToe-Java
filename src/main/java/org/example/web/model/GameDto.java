package org.example.web.model;

import org.example.domain.model.GameMode;

import java.util.UUID;

public record GameDto(
        UUID gameId,
        String[][] field,
        GameMode mode,
        String message
) {
}
