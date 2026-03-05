package org.example.domain.model;

import static org.example.domain.Configuration.SIZE;

public record GameField(Player[][] field) {

    public GameField copy() {
        GameField copy = new GameField(field);
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(this.field[i], 0, copy.field[i], 0, SIZE);
        }
        return copy;
    }
}
