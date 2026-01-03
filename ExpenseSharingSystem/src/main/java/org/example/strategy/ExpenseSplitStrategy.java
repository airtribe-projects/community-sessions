package org.example.strategy;

import org.example.model.Expense;
import org.example.model.Split;
import org.example.model.User;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for calculating expense splits.
 * Follows Strategy Pattern - allows different split algorithms.
 * Follows OCP - open for extension (new strategies), closed for modification.
 * Follows ISP - small, focused interface.
 * Follows DIP - high-level code depends on this abstraction.
 */
public interface ExpenseSplitStrategy {
    
    /**
     * Calculates how the expense should be split among participants.
     *
     * @param expense The expense to split
     * @param participants List of users participating in the expense
     * @param metadata Additional data needed for split calculation (e.g., exact amounts, percentages)
     * @return List of Split objects representing each user's share
     */
    List<Split> calculateSplits(Expense expense, List<User> participants, Map<String, Object> metadata);
    
    /**
     * Validates if the split is possible with given data.
     *
     * @param expense The expense to validate
     * @param participants List of users participating
     * @param metadata Additional data for validation
     * @return true if valid, false otherwise
     */
    boolean validate(Expense expense, List<User> participants, Map<String, Object> metadata);
}
