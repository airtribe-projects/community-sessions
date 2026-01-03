package org.example.model;

/**
 * Represents amount owed between two users.
 * Follows SRP - Only stores balance information.
 */
public class Balance {
    private final User fromUser;
    private final User toUser;
    private Double amount;

    public Balance(User fromUser, User toUser, Double amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("%s owes %s: %.2f",
                fromUser.getName(),
                toUser.getName(),
                amount);
    }
}
