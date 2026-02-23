package org.example.domain.model;

public enum Player {
    X(1),
    O(-1),
    EMPTY(0);

    private final int value;

    Player(int value) {
        this.value = value;
    }
}
