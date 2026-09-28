package com.example.inventory.model;

public enum MovementReason {
    SALE(-1), PURCHASE(1), RETURN(1), DAMAGE(-1);

    private final int sign;

    MovementReason(int sign) {
        this.sign = sign;
    }

    public int getSign() {
        return sign;
    }
}