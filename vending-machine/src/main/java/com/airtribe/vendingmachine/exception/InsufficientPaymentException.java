package com.airtribe.vendingmachine.exception;

/**
 * Exception thrown when insufficient payment is provided.
 */
public class InsufficientPaymentException extends RuntimeException {
    public InsufficientPaymentException(String message) {
        super(message);
    }
}
