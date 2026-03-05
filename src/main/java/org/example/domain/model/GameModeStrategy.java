package org.example.domain.model;

import java.util.UUID;

public interface GameModeStrategy {
    public StepResult processMove(UUID gameId, GameField newField, UUID userId);
}
