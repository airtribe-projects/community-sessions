package com.airtribe.vendingmachine.service;

import com.airtribe.vendingmachine.model.Product;

import java.util.Map;

/**
 * Service for handling display/output operations.
 * Follows SRP - Only responsible for user-facing messages.
 * Makes the system more testable and flexible for different output methods.
 */
public class DisplayService {

    public void showProductSelected(String productCode) {
        System.out.println("Product selected: " + productCode);
    }

    public void showOutOfStock() {
        System.out.println("Product is out of stock. Please select another product.");
    }

    public void showInsertMoney(double price) {
        System.out.println("Please insert ₹" + price + " to purchase this item.");
    }

    public void showCoinInserted(double value, double total) {
        System.out.println("Coin inserted: ₹" + value);
        System.out.println("Total amount: ₹" + total);
    }

    public void showNoteInserted(double value, double total) {
        System.out.println("Note inserted: ₹" + value);
        System.out.println("Total amount: ₹" + total);
    }

    public void showInsufficientPayment(double remaining) {
        System.out.println("Payment insufficient. Please insert ₹" + remaining + " more.");
    }

    public void showPaymentSufficient() {
        System.out.println("Payment sufficient. Dispensing product...");
    }

    public void showDispensingProduct(String productName) {
        System.out.println("Dispensing: " + productName);
    }

    public void showReturningChange(double change) {
        System.out.println("Returning change: ₹" + change);
    }

    public void showThankYou() {
        System.out.println("Thank you for your purchase!");
    }

    public void showTransactionCancelled() {
        System.out.println("Transaction cancelled.");
    }

    public void showRefunding(double amount) {
        System.out.println("Refunding: ₹" + amount);
        System.out.println("Please collect your money.");
    }

    public void showNoMoneyToRefund() {
        System.out.println("No money to refund.");
    }

    public void showTransactionComplete() {
        System.out.println("Transaction completed. Ready for next customer.");
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    public void showInvalidOperation(String message) {
        System.out.println(message);
    }

    public void showInventory(Map<String, Product> products, Map<String, Integer> stockInfo) {
        for (var entry : products.entrySet()) {
            Product product = entry.getValue();
            int stock = stockInfo.get(entry.getKey());
            String availability = stock > 0 ? "Available (" + stock + " left)" : "Out of Stock";
            System.out.println(String.format("[%s] %s - ₹%.2f - %s",
                product.getCode(),
                product.getName(),
                product.getPrice(),
                availability
            ));
        }
    }

    public void showProductAdded(int quantity, String name, double price) {
        System.out.println("Added " + quantity + "x " + name +
                         " to inventory at ₹" + price + " each.");
    }
}
