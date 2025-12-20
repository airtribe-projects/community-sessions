package com.airtribe.vendingmachine.model;

/**
 * Enum representing different note denominations in Indian Rupees.
 * Easily extensible for different currencies.
 */
public enum Note {
    TEN(10.0),
    TWENTY(20.0),
    FIFTY(50.0),
    HUNDRED(100.0),
    TWO_HUNDRED(200.0),
    FIVE_HUNDRED(500.0);

    private final double value;

    Note(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
