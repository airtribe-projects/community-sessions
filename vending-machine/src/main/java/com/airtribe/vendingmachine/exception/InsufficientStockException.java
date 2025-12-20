package com.airtribe.vendingmachine.exception;

/**
 * Exception thrown when product stock is insufficient.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
