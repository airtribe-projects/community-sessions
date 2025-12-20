package com.airtribe.vendingmachine.state;

/**
 * Enum representing different state types.
 * New state types can be added here without modifying existing states.
 */
public enum StateType {
    IDLE,
    HAS_MONEY,
    DISPENSE,
    REFUND
    // New states can be added here
    // MAINTENANCE,
    // OUT_OF_SERVICE,
    // etc.
}