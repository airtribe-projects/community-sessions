package com.airtribe.vendingmachine.state;

import com.airtribe.vendingmachine.VendingMachine;
import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;
import com.airtribe.vendingmachine.model.Product;

/**
 * HasMoney State - User has selected a product and can insert money.
 * Handles payment collection and validation.
 */
public class HasMoneyState extends State {

    public HasMoneyState(VendingMachine machine) {
        super(machine);
    }

    @Override
    public void selectProduct(String productCode) {
        machine.getDisplay().showInvalidOperation("Product already selected. Please complete or cancel current transaction.");
    }

    @Override
    public void insertCoin(Coin coin) {
        machine.getPayment().insertCoin(coin);
        machine.getDisplay().showCoinInserted(coin.getValue(), machine.getPayment().getTotalAmount());

        checkPaymentAndDispense();
    }

    @Override
    public void insertNote(Note note) {
        machine.getPayment().insertNote(note);
        machine.getDisplay().showNoteInserted(note.getValue(), machine.getPayment().getTotalAmount());

        checkPaymentAndDispense();
    }

    @Override
    public void cancelTransaction() {
        machine.getDisplay().showTransactionCancelled();
        machine.transitionTo(StateType.REFUND);
        machine.getCurrentState().refund();
    }

    @Override
    public void dispenseProduct() {
        machine.getDisplay().showInvalidOperation("Please insert sufficient payment first.");
    }

    @Override
    public void refund() {
        machine.getDisplay().showInvalidOperation("Use cancel transaction to get a refund.");
    }

    /**
     * Helper method to check if payment is sufficient and transition to dispense state.
     */
    private void checkPaymentAndDispense() {
        String productCode = machine.getSelectedProduct();
        Product product = machine.getProduct(productCode);

        if (machine.getPayment().isSufficient(product.getPrice())) {
            machine.getDisplay().showPaymentSufficient();
            machine.transitionTo(StateType.DISPENSE);
            machine.getCurrentState().dispenseProduct();
        } else {
            double remaining = product.getPrice() - machine.getPayment().getTotalAmount();
            remaining = Math.round(remaining * 100.0) / 100.0;
            machine.getDisplay().showInsufficientPayment(remaining);
        }
    }
}
