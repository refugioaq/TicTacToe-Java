package org.example.web.model;

import java.util.UUID;

public record GameDto(
        UUID gameId,
        String[][] field,
        String message
) {
}
