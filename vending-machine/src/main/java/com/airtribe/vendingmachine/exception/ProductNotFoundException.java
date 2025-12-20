package com.airtribe.vendingmachine.exception;

/**
 * Exception thrown when product is not found in inventory.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
