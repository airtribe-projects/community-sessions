package com.airtribe.vendingmachine.model;

/**
 * Enum representing different coin denominations in Indian Rupees.
 * Easily extensible for different currencies.
 */
public enum Coin {
    ONE(1.0),
    TWO(2.0),
    FIVE(5.0),
    TEN(10.0);

    private final double value;

    Coin(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
