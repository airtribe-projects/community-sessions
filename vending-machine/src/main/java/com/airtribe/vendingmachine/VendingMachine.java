package com.airtribe.vendingmachine;

import com.airtribe.vendingmachine.inventory.Inventory;
import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;
import com.airtribe.vendingmachine.model.Product;
import com.airtribe.vendingmachine.payment.Payment;
import com.airtribe.vendingmachine.service.DisplayService;
import com.airtribe.vendingmachine.state.*;

/**
 * Main VendingMachine class that orchestrates all components.
 * Follows SRP - Delegates responsibilities to specialized components.
 * Follows OCP - New states can be added via StateManager without modifying this class.
 */
public class VendingMachine {
    private final Inventory inventory;
    private final Payment payment;
    private final DisplayService displayService;
    private final StateManager stateManager;

    // Current state
    private State currentState;

    // Currently selected product
    private String selectedProduct;

    public VendingMachine() {
        this.inventory = new Inventory();
        this.payment = new Payment();
        this.displayService = new DisplayService();

        this.stateManager = new StateManager(this);

        this.currentState = stateManager.getState(StateType.IDLE);
        this.selectedProduct = null;
    }

    /**
     * Select a product by its code.
     */
    public void selectProduct(String productCode) {
        currentState.selectProduct(productCode);
    }

    /**
     * Insert a coin into the machine.
     */
    public void insertCoin(Coin coin) {
        currentState.insertCoin(coin);
    }

    /**
     * Insert a note into the machine.
     */
    public void insertNote(Note note) {
        currentState.insertNote(note);
    }

    /**
     * Cancel the current transaction.
     */
    public void cancelTransaction() {
        currentState.cancelTransaction();
    }

    // Inventory management methods
    public void addProduct(Product product, int quantity) {
        inventory.addProduct(product, quantity);
        displayService.showProductAdded(quantity, product.getName(), product.getPrice());
    }

    public void displayInventory() {
        displayService.showInventory(inventory.getAllProducts(), inventory.getStockInfo());
    }

    // Public methods for states to use
    public State getCurrentState() {
        return currentState;
    }

    public void setState(State state) {
        this.currentState = state;
    }

    /**
     * Transition to a specific state type.
     */
    public void transitionTo(StateType stateType) {
        this.currentState = stateManager.getState(stateType);
    }

    public DisplayService getDisplay() {
        return displayService;
    }

    public Payment getPayment() {
        return payment;
    }

    public String getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(String productCode) {
        this.selectedProduct = productCode;
    }

    public Product getProduct(String productCode) {
        return inventory.getProduct(productCode);
    }

    public boolean isProductAvailable(String productCode) {
        return inventory.isAvailable(productCode);
    }

    public void dispenseProduct(String productCode) {
        inventory.dispenseProduct(productCode);
    }
}
