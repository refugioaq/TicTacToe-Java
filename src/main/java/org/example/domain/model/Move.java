package org.example.domain.model;

public record Move(int row, int col) {
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
