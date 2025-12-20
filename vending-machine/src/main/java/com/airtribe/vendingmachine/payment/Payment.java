package com.airtribe.vendingmachine.payment;

import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles payment collection and refund calculation.
 * Follows SRP - Only responsible for payment management.
 */
public class Payment {
    private double totalAmount;
    private final List<Coin> coinsInserted;
    private final List<Note> notesInserted;

    public Payment() {
        this.totalAmount = 0.0;
        this.coinsInserted = new ArrayList<>();
        this.notesInserted = new ArrayList<>();
    }

    /**
     * Adds a coin to the payment.
     */
    public void insertCoin(Coin coin) {
        if (coin == null) {
            throw new IllegalArgumentException("Coin cannot be null");
        }
        coinsInserted.add(coin);
        totalAmount += coin.getValue();
        totalAmount = Math.round(totalAmount * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Adds a note to the payment.
     */
    public void insertNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        notesInserted.add(note);
        totalAmount += note.getValue();
        totalAmount = Math.round(totalAmount * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Gets the total amount collected.
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Checks if the payment is sufficient for the given price.
     */
    public boolean isSufficient(double price) {
        return totalAmount >= price;
    }

    /**
     * Calculates the change to be returned.
     */
    public double calculateChange(double price) {
        double change = totalAmount - price;
        return Math.round(change * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Resets the payment (used for refund or after successful transaction).
     */
    public void reset() {
        totalAmount = 0.0;
        coinsInserted.clear();
        notesInserted.clear();
    }

    /**
     * Gets all coins inserted.
     */
    public List<Coin> getCoinsInserted() {
        return new ArrayList<>(coinsInserted);
    }

    /**
     * Gets all notes inserted.
     */
    public List<Note> getNotesInserted() {
        return new ArrayList<>(notesInserted);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "totalAmount=" + totalAmount +
                ", coins=" + coinsInserted.size() +
                ", notes=" + notesInserted.size() +
                '}';
    }
}
