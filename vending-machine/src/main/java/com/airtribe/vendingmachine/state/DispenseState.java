package com.airtribe.vendingmachine.state;

import com.airtribe.vendingmachine.VendingMachine;
import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;
import com.airtribe.vendingmachine.model.Product;

/**
 * Dispense State - Payment is sufficient, product will be dispensed.
 * Handles product dispensing and change return.
 */
public class DispenseState extends State {

    public DispenseState(VendingMachine machine) {
        super(machine);
    }

    @Override
    public void selectProduct(String productCode) {
        machine.getDisplay().showInvalidOperation("Currently dispensing product. Please wait.");
    }

    @Override
    public void insertCoin(Coin coin) {
        machine.getDisplay().showInvalidOperation("Product is being dispensed. Cannot accept more money.");
    }

    @Override
    public void insertNote(Note note) {
        machine.getDisplay().showInvalidOperation("Product is being dispensed. Cannot accept more money.");
    }

    @Override
    public void cancelTransaction() {
        machine.getDisplay().showInvalidOperation("Cannot cancel. Product is being dispensed.");
    }

    @Override
    public void dispenseProduct() {
        try {
            String productCode = machine.getSelectedProduct();
            Product product = machine.getProduct(productCode);

            // Dispense the product
            machine.dispenseProduct(productCode);
            machine.getDisplay().showDispensingProduct(product.getName());

            // Calculate and return change
            double change = machine.getPayment().calculateChange(product.getPrice());
            if (change > 0) {
                machine.getDisplay().showReturningChange(change);
            }

            // Reset payment and selected product
            machine.getPayment().reset();
            machine.setSelectedProduct(null);

            // Return to idle state
            machine.transitionTo(StateType.IDLE);
            machine.getDisplay().showThankYou();

        } catch (Exception e) {
            machine.getDisplay().showError(e.getMessage());
            machine.transitionTo(StateType.REFUND);
            machine.getCurrentState().refund();
        }
    }

    @Override
    public void refund() {
        machine.getDisplay().showInvalidOperation("Cannot refund. Product is being dispensed.");
    }
}
