package com.airtribe.vendingmachine.state;

import com.airtribe.vendingmachine.VendingMachine;
import com.airtribe.vendingmachine.model.Coin;
import com.airtribe.vendingmachine.model.Note;

/**
 * Refund State - Transaction is cancelled, refund is being processed.
 * Returns all inserted money and resets the machine.
 */
public class RefundState extends State {

    public RefundState(VendingMachine machine) {
        super(machine);
    }

    @Override
    public void selectProduct(String productCode) {
        machine.getDisplay().showInvalidOperation("Refund in progress. Please wait.");
    }

    @Override
    public void insertCoin(Coin coin) {
        machine.getDisplay().showInvalidOperation("Refund in progress. Cannot accept money.");
    }

    @Override
    public void insertNote(Note note) {
        machine.getDisplay().showInvalidOperation("Refund in progress. Cannot accept money.");
    }

    @Override
    public void cancelTransaction() {
        machine.getDisplay().showInvalidOperation("Transaction already cancelled.");
    }

    @Override
    public void dispenseProduct() {
        machine.getDisplay().showInvalidOperation("Transaction cancelled. No product will be dispensed.");
    }

    @Override
    public void refund() {
        double refundAmount = machine.getPayment().getTotalAmount();

        if (refundAmount > 0) {
            machine.getDisplay().showRefunding(refundAmount);
        } else {
            machine.getDisplay().showNoMoneyToRefund();
        }

        // Reset payment and selected product
        machine.getPayment().reset();
        machine.setSelectedProduct(null);

        // Return to idle state
        machine.transitionTo(StateType.IDLE);
        machine.getDisplay().showTransactionComplete();
    }
}
