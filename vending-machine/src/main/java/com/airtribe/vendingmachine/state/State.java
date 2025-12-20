package com.airtribe.vendingmachine.state;

import com.airtribe.vendingmachine.VendingMachine;
import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;

/**
 * Abstract State class for State Pattern.
 * Follows OCP - New states can be added without modifying existing code.
 * Follows LSP - All concrete states can substitute this base class.
 */
public abstract class State {
    protected VendingMachine machine;

    public State(VendingMachine machine) {
        this.machine = machine;
    }

    /**
     * Handle product selection.
     */
    public abstract void selectProduct(String productCode);

    /**
     * Handle coin insertion.
     */
    public abstract void insertCoin(Coin coin);

    /**
     * Handle note insertion.
     */
    public abstract void insertNote(Note note);

    /**
     * Handle transaction cancellation.
     */
    public abstract void cancelTransaction();

    /**
     * Handle product dispensing.
     */
    public abstract void dispenseProduct();

    /**
     * Handle refund.
     */
    public abstract void refund();
}
