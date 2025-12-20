package com.airtribe.vendingmachine.state;

import com.airtribe.vendingmachine.VendingMachine;
import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;
import com.airtribe.vendingmachine.model.Product;

/**
 * Idle State - The default state when vending machine is waiting for user input.
 * User can select a product in this state.
 */
public class IdleState extends State {

    public IdleState(VendingMachine machine) {
        super(machine);
    }

    @Override
    public void selectProduct(String productCode) {
        machine.getDisplay().showProductSelected(productCode);

        if (!machine.isProductAvailable(productCode)) {
            machine.getDisplay().showOutOfStock();
            return;
        }

        machine.setSelectedProduct(productCode);
        machine.transitionTo(StateType.HAS_MONEY);

        Product product = machine.getProduct(productCode);
        machine.getDisplay().showInsertMoney(product.getPrice());
    }

    @Override
    public void insertCoin(Coin coin) {
        machine.getDisplay().showInvalidOperation("Please select a product first.");
    }

    @Override
    public void insertNote(Note note) {
        machine.getDisplay().showInvalidOperation("Please select a product first.");
    }

    @Override
    public void cancelTransaction() {
        machine.getDisplay().showInvalidOperation("No transaction to cancel.");
    }

    @Override
    public void dispenseProduct() {
        machine.getDisplay().showInvalidOperation("Please select a product first.");
    }

    @Override
    public void refund() {
        machine.getDisplay().showInvalidOperation("No money to refund.");
    }
}
