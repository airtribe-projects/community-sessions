package org.example.model;

/**
 * Represents an individual's share in an expense.
 * Follows SRP - Only stores split information.
 */
public class Split {
    private final User user;
    private Double amount;
    private Double percentage;

    public Split(User user, Double amount) {
        this.user = user;
        this.amount = amount;
        this.percentage = null;
    }

    public Split(User user, Double amount, Double percentage) {
        this.user = user;
        this.amount = amount;
        this.percentage = percentage;
    }

    public User getUser() {
        return user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPercentage() {
        return percentage;
    }

}
