package org.example.strategy;

import org.example.model.Expense;
import org.example.model.Split;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for splitting expense with exact amounts specified for each user.
 * Example: Alice pays 500, Bob pays 1000, Charlie pays 1500 (total 3000)
 */
public class ExactSplitStrategy implements ExpenseSplitStrategy {

    @Override
    public List<Split> calculateSplits(Expense expense, List<User> participants, Map<String, Object> metadata) {
        if (!validate(expense, participants, metadata)) {
            throw new IllegalArgumentException("Invalid expense data for exact split");
        }

        @SuppressWarnings("unchecked")
        Map<User, Double> exactAmounts = (Map<User, Double>) metadata.get("exactAmounts");

        List<Split> splits = new ArrayList<>();
        for (User user : participants) {
            Double amount = exactAmounts.get(user);
            if (amount == null) {
                throw new IllegalArgumentException("Amount not specified for user: " + user.getName());
            }
            splits.add(new Split(user, amount));
        }

        return splits;
    }

    @Override
    public boolean validate(Expense expense, List<User> participants, Map<String, Object> metadata) {
        if (expense == null || expense.getTotalAmount() <= 0 ||
                participants == null || participants.isEmpty() ||
                metadata == null || !metadata.containsKey("exactAmounts")) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Map<User, Double> exactAmounts = (Map<User, Double>) metadata.get("exactAmounts");

        if (exactAmounts == null || exactAmounts.isEmpty()) {
            return false;
        }

        // Validate that sum of exact amounts equals total amount
        double sum = exactAmounts.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Allow small rounding difference (0.01)
        return Math.abs(sum - expense.getTotalAmount()) < 0.01;
    }
}
