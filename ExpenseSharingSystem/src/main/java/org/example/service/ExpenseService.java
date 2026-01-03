package org.example.service;

import org.example.model.*;
import org.example.strategy.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing expense operations.
 * Follows SRP - Only handles expense operations.
 * Follows OCP - Can add new split types without modification.
 * Follows DIP - Depends on ExpenseSplitStrategy interface.
 */
public class ExpenseService {
    private final List<Expense> expenses;
    private final BalanceManager balanceManager;
    private final Map<SplitType, ExpenseSplitStrategy> strategyMap;
    private int expenseIdCounter;

    public ExpenseService(BalanceManager balanceManager) {
        this.expenses = new ArrayList<>();
        this.balanceManager = balanceManager;
        this.strategyMap = new HashMap<>();
        this.expenseIdCounter = 1;

        // Register strategies
        registerStrategy(SplitType.EQUAL, new EqualSplitStrategy());
        registerStrategy(SplitType.EXACT, new ExactSplitStrategy());
        registerStrategy(SplitType.PERCENTAGE, new PercentageSplitStrategy());
    }

    /**
     * Core method to create an expense using Strategy Pattern.
     * This method delegates split calculation to the appropriate strategy.
     * Follows OCP - can handle any split type without modification.
     */
    public Expense createExpense(String description, Double amount, User paidBy,
                                  List<User> participants, SplitType splitType, 
                                  Map<String, Object> metadata, Group group) {
        ExpenseSplitStrategy strategy = strategyMap.get(splitType);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for split type: " + splitType);
        }

        // Create temporary expense for validation
        String expenseId = generateExpenseId();
        Expense tempExpense = new Expense(expenseId, description, amount, paidBy,
                new ArrayList<>(), splitType, group);

        // Calculate splits using the strategy
        List<Split> splits = strategy.calculateSplits(tempExpense, participants, metadata);

        // Create final expense with splits
        Expense expense = new Expense(expenseId, description, amount, paidBy,
                splits, splitType, group);

        expenses.add(expense);
        balanceManager.updateBalances(expense);

        if (group != null) {
            group.addExpense(expense);
        }

        System.out.println("✓ Expense created: " + expense);
        printExpenseSplits(expense);

        return expense;
    }

    /**
     * Convenience method: Creates an expense with equal split.
     */
    public Expense createEqualExpense(String description, Double amount, User paidBy,
                                      List<User> participants, Group group) {
        return createExpense(description, amount, paidBy, participants, 
                           SplitType.EQUAL, new HashMap<>(), group);
    }

    /**
     * Convenience method: Creates an expense with exact amounts.
     */
    public Expense createExactExpense(String description, Double amount, User paidBy,
                                      Map<User, Double> exactAmounts, Group group) {
        List<User> participants = new ArrayList<>(exactAmounts.keySet());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("exactAmounts", exactAmounts);
        
        return createExpense(description, amount, paidBy, participants, 
                           SplitType.EXACT, metadata, group);
    }

    /**
     * Convenience method: Creates an expense with percentage-based split.
     */
    public Expense createPercentageExpense(String description, Double amount, User paidBy,
                                           Map<User, Double> percentages, Group group) {
        List<User> participants = new ArrayList<>(percentages.keySet());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("percentages", percentages);
        
        return createExpense(description, amount, paidBy, participants, 
                           SplitType.PERCENTAGE, metadata, group);
    }

    /**
     * Retrieves an expense by ID.
     */
    public Expense getExpenseById(String id) {
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Expense not found: " + id));
    }

    /**
     * Gets all expenses involving a specific user.
     */
    public List<Expense> getExpensesByUser(User user) {
        return expenses.stream()
                .filter(e -> e.involves(user))
                .collect(Collectors.toList());
    }

    /**
     * Gets all expenses in a specific group.
     */
    public List<Expense> getExpensesByGroup(Group group) {
        return expenses.stream()
                .filter(e -> e.getGroup() != null && e.getGroup().equals(group))
                .collect(Collectors.toList());
    }

    /**
     * Gets all expenses in the system.
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Registers a new split strategy.
     * Demonstrates OCP - can add new strategies without modifying this class.
     */
    private void registerStrategy(SplitType splitType, ExpenseSplitStrategy strategy) {
        strategyMap.put(splitType, strategy);
    }

    private String generateExpenseId() {
        return "E" + String.format("%04d", expenseIdCounter++);
    }

    private void printExpenseSplits(Expense expense) {
        System.out.println("  Splits:");
        for (Split split : expense.getSplits()) {
            System.out.println("    - " + split);
        }
    }
}
