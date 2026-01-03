package org.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single expense transaction.
 * Immutable after creation to ensure data consistency.
 * Follows SRP - Only stores expense data, not split logic.
 */
public class Expense {
    private final String id;
    private final String description;
    private final Double totalAmount;
    private final User paidBy;
    private final List<Split> splits;
    private final SplitType splitType;
    private final Group group;
    private final LocalDateTime timestamp;

    public Expense(String id, String description, Double totalAmount, User paidBy,
                   List<Split> splits, SplitType splitType, Group group) {
        this.id = id;
        this.description = description;
        this.totalAmount = totalAmount;
        this.paidBy = paidBy;
        this.splits = new ArrayList<>(splits); // Defensive copy
        this.splitType = splitType;
        this.group = group;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public List<Split> getSplits() {
        return new ArrayList<>(splits); // Return defensive copy
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public Group getGroup() {
        return group;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Validates if the expense is valid.
     */
    public boolean isValid() {
        return totalAmount > 0 && paidBy != null && !splits.isEmpty();
    }

    /**
     * Checks if a user is involved in this expense.
     */
    public boolean involves(User user) {
        if (paidBy.equals(user)) {
            return true;
        }
        return splits.stream().anyMatch(split -> split.getUser().equals(user));
    }
}
