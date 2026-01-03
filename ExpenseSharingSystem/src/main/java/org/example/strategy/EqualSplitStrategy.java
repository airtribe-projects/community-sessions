package org.example.strategy;

import org.example.model.Expense;
import org.example.model.Split;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for splitting expense equally among all participants.
 * Example: 3000 split among 3 people = 1000 each
 */
public class EqualSplitStrategy implements ExpenseSplitStrategy {

    @Override
    public List<Split> calculateSplits(Expense expense, List<User> participants, Map<String, Object> metadata) {
        if (!validate(expense, participants, metadata)) {
            throw new IllegalArgumentException("Invalid expense data for equal split");
        }

        List<Split> splits = new ArrayList<>();
        int participantCount = participants.size();
        double amountPerPerson = expense.getTotalAmount() / participantCount;

        // Handle rounding - last person gets the remainder
        double totalAssigned = 0;
        for (int i = 0; i < participantCount - 1; i++) {
            double roundedAmount = Math.round(amountPerPerson * 100.0) / 100.0;
            splits.add(new Split(participants.get(i), roundedAmount));
            totalAssigned += roundedAmount;
        }

        // Last person gets the remainder to ensure total matches
        double lastPersonAmount = expense.getTotalAmount() - totalAssigned;
        splits.add(new Split(participants.get(participantCount - 1), lastPersonAmount));

        return splits;
    }

    @Override
    public boolean validate(Expense expense, List<User> participants, Map<String, Object> metadata) {
        return expense != null &&
                expense.getTotalAmount() > 0 &&
                participants != null &&
                !participants.isEmpty();
    }
}
