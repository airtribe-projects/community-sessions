package org.example.strategy;

import org.example.model.Expense;
import org.example.model.Split;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for splitting expense based on percentages.
 * Example: Alice 20%, Bob 30%, Charlie 50% of 3000 = 600, 900, 1500
 */
public class PercentageSplitStrategy implements ExpenseSplitStrategy {

    @Override
    public List<Split> calculateSplits(Expense expense, List<User> participants, Map<String, Object> metadata) {
        if (!validate(expense, participants, metadata)) {
            throw new IllegalArgumentException("Invalid expense data for percentage split");
        }

        @SuppressWarnings("unchecked")
        Map<User, Double> percentages = (Map<User, Double>) metadata.get("percentages");

        List<Split> splits = new ArrayList<>();
        double totalAmount = expense.getTotalAmount();
        double totalAssigned = 0;

        // Calculate amounts for all but last participant
        List<User> participantList = new ArrayList<>(participants);
        for (int i = 0; i < participantList.size() - 1; i++) {
            User user = participantList.get(i);
            Double percentage = percentages.get(user);
            if (percentage == null) {
                throw new IllegalArgumentException("Percentage not specified for user: " + user.getName());
            }
            double amount = Math.round((totalAmount * percentage / 100.0) * 100.0) / 100.0;
            splits.add(new Split(user, amount, percentage));
            totalAssigned += amount;
        }

        // Last participant gets remainder to handle rounding
        User lastUser = participantList.get(participantList.size() - 1);
        Double lastPercentage = percentages.get(lastUser);
        double lastAmount = totalAmount - totalAssigned;
        splits.add(new Split(lastUser, lastAmount, lastPercentage));

        return splits;
    }

    @Override
    public boolean validate(Expense expense, List<User> participants, Map<String, Object> metadata) {
        if (expense == null || expense.getTotalAmount() <= 0 ||
                participants == null || participants.isEmpty() ||
                metadata == null || !metadata.containsKey("percentages")) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Map<User, Double> percentages = (Map<User, Double>) metadata.get("percentages");

        if (percentages == null || percentages.isEmpty()) {
            return false;
        }

        // Validate that sum of percentages equals 100
        double sum = percentages.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Allow small rounding difference (0.01%)
        return Math.abs(sum - 100.0) < 0.01;
    }
}
