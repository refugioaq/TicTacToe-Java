package org.example.web.model;

import org.example.domain.model.GameMode;

import java.time.LocalDateTime;
import java.util.UUID;

public record GameDto(
        UUID gameId,
        UUID idFirstPlayer,
        UUID idSecondPlayer,
        UUID winner,
        String[][] field,
        GameMode mode,
        String message,
        LocalDateTime createdAt
) {}
