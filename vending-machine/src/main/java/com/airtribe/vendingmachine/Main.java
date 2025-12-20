package com.airtribe.vendingmachine;

import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;
import com.airtribe.vendingmachine.model.Product;

/**
 * Main class to demonstrate the Vending Machine functionality.
 * Demonstrates all functional requirements:
 * 1. Inventory management
 * 2. Product dispensing
 * 3. Payment collection and change return
 * 4. Transaction cancellation
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("   VENDING MACHINE SIMULATION");

        VendingMachine vendingMachine = new VendingMachine();

        System.out.println(">>> Setting up inventory...\n");
        vendingMachine.addProduct(new Product("A1", "Coke", 40.0), 5);
        vendingMachine.addProduct(new Product("A2", "Pepsi", 40.0), 5);
        vendingMachine.addProduct(new Product("B1", "Water", 20.0), 3);
        vendingMachine.addProduct(new Product("B2", "Juice", 50.0), 2);
        vendingMachine.addProduct(new Product("C1", "Chips", 30.0), 4);
        vendingMachine.addProduct(new Product("C2", "Candy", 15.0), 0); // Out of stock

        vendingMachine.displayInventory();

        // SCENARIO 1: Successful purchase with exact amount
        System.out.println("SCENARIO 1: Successful purchase (exact amount)");

        vendingMachine.selectProduct("B1"); // Water - ₹20
        vendingMachine.insertNote(Note.TWENTY);

        System.out.println();

        // SCENARIO 2: Successful purchase with change

        System.out.println("SCENARIO 2: Successful purchase (with change)");

        vendingMachine.selectProduct("C1"); // Chips - ₹30
        vendingMachine.insertNote(Note.FIFTY);

        System.out.println();

        // SCENARIO 3: Purchase with multiple coins
        System.out.println("SCENARIO 3: Purchase with multiple coins");
        vendingMachine.selectProduct("A1"); // Coke - ₹40
        vendingMachine.insertCoin(Coin.TEN);
        vendingMachine.insertCoin(Coin.TEN);
        vendingMachine.insertCoin(Coin.TEN);
        vendingMachine.insertCoin(Coin.TEN);

        System.out.println();

        // SCENARIO 4: Transaction cancellation
        System.out.println("SCENARIO 4: Transaction cancellation");

        vendingMachine.selectProduct("B2"); // Juice - ₹50
        vendingMachine.insertNote(Note.TWENTY);
        System.out.println("\nUser decides to cancel...\n");
        vendingMachine.cancelTransaction();

        System.out.println();

        // SCENARIO 5: Try to purchase out of stock item
        System.out.println("SCENARIO 5: Out of stock item");

        vendingMachine.selectProduct("C2"); // Candy - Out of stock

        System.out.println();

        // SCENARIO 6: Invalid operations in different states
        System.out.println("SCENARIO 6: Invalid operations");

        System.out.println("Trying to insert money without selecting product:");
        vendingMachine.insertCoin(Coin.FIVE);
        System.out.println();

        vendingMachine.selectProduct("A2"); // Pepsi - ₹40
        System.out.println("\nTrying to select another product:");
        vendingMachine.selectProduct("B1");
        System.out.println();

        vendingMachine.cancelTransaction();

        // Display final inventory
        System.out.println("FINAL INVENTORY STATUS");
        vendingMachine.displayInventory();

        System.out.println("   SIMULATION COMPLETE");
    }
}
