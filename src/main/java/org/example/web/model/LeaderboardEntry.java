package org.example.web.model;

import java.util.UUID;

public record LeaderboardEntry(
        UUID userId,
        String login,
        String winRate
) {}