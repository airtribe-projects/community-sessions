package org.example.service;

import org.example.model.Balance;
import org.example.model.Expense;
import org.example.model.Split;
import org.example.model.User;

import java.util.*;

/**
 * Service for managing balance calculations and debt tracking.
 * Follows SRP - Only handles balance management.
 */
public class BalanceManager {
    // Nested map: userId -> (otherUserId -> amount)
    // Positive amount means userId owes otherUserId
    private final Map<String, Map<String, Double>> balances;
    // Cache to resolve User objects from IDs (needed for simplification)
    private final Map<String, User> userCache;

    public BalanceManager() {
        this.balances = new HashMap<>();
        this.userCache = new HashMap<>();
    }

    /**
     * Updates balances when a new expense is created.
     */
    public void updateBalances(Expense expense) {
        User paidBy = expense.getPaidBy();
        // Cache the user for later lookups
        userCache.put(paidBy.getId(), paidBy);

        for (Split split : expense.getSplits()) {
            User user = split.getUser();
            Double amount = split.getAmount();
            
            // Cache the user for later lookups
            userCache.put(user.getId(), user);

            // Skip if user paid for themselves
            if (user.equals(paidBy)) {
                continue;
            }

            // User owes paidBy the split amount
            updateBalance(user, paidBy, amount);
        }
    }

    /**
     * Gets all balances for a specific user.
     * Returns map of User -> Amount (positive means user owes them, negative means they owe user)
     */
    public Map<User, Double> getUserBalances(User user) {
        Map<User, Double> userBalances = new HashMap<>();
        String userId = user.getId();

        // Get amounts this user owes to others
        Map<String, Double> owes = balances.getOrDefault(userId, new HashMap<>());
        for (Map.Entry<String, Double> entry : owes.entrySet()) {
            // This is simplified - in real system, would need to resolve User from ID
            // For now, we'll return the balance amount
        }

        return userBalances;
    }

    /**
     * Gets all balances in the system.
     */
    public List<Balance> getAllBalances() {
        List<Balance> allBalances = new ArrayList<>();

        for (Map.Entry<String, Map<String, Double>> entry : balances.entrySet()) {
            String fromUserId = entry.getKey();

            for (Map.Entry<String, Double> balanceEntry : entry.getValue().entrySet()) {
                String toUserId = balanceEntry.getKey();
                Double amount = balanceEntry.getValue();

                if (amount > 0.01) { // Only include non-zero balances
                    // Note: In real implementation, would resolve User objects from IDs
                    // For now, storing IDs in Balance (would need to enhance Balance class)
                }
            }
        }

        return allBalances;
    }

    /**
     * Gets balance between two users.
     * Positive means user1 owes user2, negative means user2 owes user1.
     */
    public Double getBalance(User user1, User user2) {
        String key1 = getBalanceKey(user1, user2);
        String key2 = getBalanceKey(user2, user1);

        double balance1 = balances.getOrDefault(user1.getId(), new HashMap<>())
                .getOrDefault(user2.getId(), 0.0);
        double balance2 = balances.getOrDefault(user2.getId(), new HashMap<>())
                .getOrDefault(user1.getId(), 0.0);

        return balance1 - balance2;
    }

    /**
     * Records a payment from one user to another.
     */
    public void recordPayment(User fromUser, User toUser, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Reduce the balance
        updateBalance(fromUser, toUser, -amount);

        System.out.println(String.format("✓ Payment recorded: %s paid %.2f to %s",
                fromUser.getName(), amount, toUser.getName()));
    }

    /**
     * Simplifies debts to minimize number of transactions using Greedy Algorithm.
     * 
     * Algorithm:
     * 1. Calculate net balance for each user (positive = owed money, negative = owes money)
     * 2. Separate users into creditors (positive) and debtors (negative)
     * 3. Repeatedly match the largest creditor with the largest debtor
     * 4. Settle as much as possible between them
     * 5. Continue until all debts are settled
     * 
     * Time Complexity: O(n²) where n is number of users
     * This minimizes the number of transactions needed.
     */
    public List<Balance> simplifyDebts() {
        // Step 1: Calculate net balance for each user
        Map<String, Double> netBalances = calculateNetBalances();

        // Step 2: Separate into creditors and debtors
        List<DebtNode> creditors = new ArrayList<>();  // People who should receive money
        List<DebtNode> debtors = new ArrayList<>();    // People who should pay money

        for (Map.Entry<String, Double> entry : netBalances.entrySet()) {
            String userId = entry.getKey();
            Double netAmount = entry.getValue();

            if (netAmount > 0.01) {
                // This user is owed money (creditor)
                creditors.add(new DebtNode(userId, netAmount));
            } else if (netAmount < -0.01) {
                // This user owes money (debtor)
                debtors.add(new DebtNode(userId, Math.abs(netAmount)));
            }
        }

        // Step 3: Greedy matching - match largest creditor with largest debtor
        List<Balance> simplifiedBalances = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            // Sort to get largest amounts (greedy approach)
            creditors.sort((a, b) -> Double.compare(b.amount, a.amount));
            debtors.sort((a, b) -> Double.compare(b.amount, a.amount));

            DebtNode creditor = creditors.get(0);
            DebtNode debtor = debtors.get(0);

            // Settle as much as possible between them
            double settleAmount = Math.min(creditor.amount, debtor.amount);

            // Create balance record
            User fromUser = userCache.get(debtor.userId);
            User toUser = userCache.get(creditor.userId);
            
            if (fromUser != null && toUser != null) {
                simplifiedBalances.add(new Balance(fromUser, toUser, settleAmount));
            }

            // Update amounts
            creditor.amount -= settleAmount;
            debtor.amount -= settleAmount;

            // Remove if settled
            if (creditor.amount < 0.01) {
                creditors.remove(0);
            }
            if (debtor.amount < 0.01) {
                debtors.remove(0);
            }
        }

        return simplifiedBalances;
    }

    /**
     * Calculates net balance for each user.
     * Positive balance = user is owed money (creditor)
     * Negative balance = user owes money (debtor)
     */
    private Map<String, Double> calculateNetBalances() {
        Map<String, Double> netBalances = new HashMap<>();

        // Initialize all users with 0 balance
        for (String userId : userCache.keySet()) {
            netBalances.put(userId, 0.0);
        }

        // Calculate net balances
        for (Map.Entry<String, Map<String, Double>> entry : balances.entrySet()) {
            String userId = entry.getKey();
            double currentNet = netBalances.getOrDefault(userId, 0.0);

            // Amount this user owes (negative contribution)
            for (Double amount : entry.getValue().values()) {
                currentNet -= amount;
            }

            netBalances.put(userId, currentNet);
        }

        // Add amounts owed to each user (positive contribution)
        for (Map.Entry<String, Map<String, Double>> entry : balances.entrySet()) {
            for (Map.Entry<String, Double> balanceEntry : entry.getValue().entrySet()) {
                String toUserId = balanceEntry.getKey();
                Double amount = balanceEntry.getValue();
                
                double currentNet = netBalances.getOrDefault(toUserId, 0.0);
                netBalances.put(toUserId, currentNet + amount);
            }
        }

        return netBalances;
    }

    /**
     * Helper class to track debt amounts during simplification.
     */
    private static class DebtNode {
        String userId;
        double amount;

        DebtNode(String userId, double amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

    /**
     * Updates balance between two users.
     */
    private void updateBalance(User fromUser, User toUser, Double amount) {
        String fromId = fromUser.getId();
        String toId = toUser.getId();

        balances.putIfAbsent(fromId, new HashMap<>());
        Map<String, Double> fromBalances = balances.get(fromId);

        double currentBalance = fromBalances.getOrDefault(toId, 0.0);
        double newBalance = currentBalance + amount;

        if (Math.abs(newBalance) < 0.01) {
            fromBalances.remove(toId);
        } else {
            fromBalances.put(toId, newBalance);
        }
    }

    private String getBalanceKey(User user1, User user2) {
        return user1.getId() + "-" + user2.getId();
    }

    /**
     * Prints all balances in a readable format.
     */
    public void printAllBalances() {
        System.out.println("\n=== Current Balances ===");
        boolean hasBalances = false;

        for (Map.Entry<String, Map<String, Double>> entry : balances.entrySet()) {
            String fromId = entry.getKey();

            for (Map.Entry<String, Double> balanceEntry : entry.getValue().entrySet()) {
                String toId = balanceEntry.getKey();
                Double amount = balanceEntry.getValue();

                if (amount > 0.01) {
                    System.out.println(String.format("  User %s owes User %s: %.2f",
                            fromId, toId, amount));
                    hasBalances = true;
                }
            }
        }

        if (!hasBalances) {
            System.out.println("  No outstanding balances");
        }
        System.out.println();
    }

    /**
     * Prints simplified debts showing minimum transactions needed.
     */
    public void printSimplifiedDebts() {
        List<Balance> simplified = simplifyDebts();
        
        System.out.println("\n=== Simplified Debts (Minimum Transactions) ===");
        if (simplified.isEmpty()) {
            System.out.println("  No outstanding balances");
        } else {
            System.out.println("  Total transactions needed: " + simplified.size());
            for (Balance balance : simplified) {
                System.out.println("  " + balance);
            }
        }
        System.out.println();
    }
}
