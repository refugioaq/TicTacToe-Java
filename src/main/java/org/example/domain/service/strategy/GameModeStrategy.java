package org.example.domain.service.strategy;

import org.example.domain.model.GameField;
import org.example.domain.model.StepResult;

import java.util.UUID;

public interface GameModeStrategy {
    public StepResult processMove(UUID gameId, GameField newField, UUID userId);
}
