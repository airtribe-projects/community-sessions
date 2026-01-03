package org.example;

import org.example.model.*;
import org.example.service.*;

import java.util.*;

/**
 * Main class demonstrating the Expense Sharing System.
 * This demo showcases all features and design principles.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     EXPENSE SHARING SYSTEM - SPLITWISE CLONE              ║");
        System.out.println("║     Demonstrating SOLID Principles & Design Patterns      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Initialize services
        BalanceManager balanceManager = new BalanceManager();
        ExpenseService expenseService = new ExpenseService(balanceManager);
        GroupService groupService = new GroupService(expenseService);
        UserService userService = new UserService();

        try {
            // Demo 1: Create Users
            demo1_CreateUsers(userService);

            // Demo 2: Create Group
            Group tripGroup = demo2_CreateGroup(groupService, userService);

            // Demo 3: Equal Split Expense
            demo3_EqualSplit(expenseService, userService, tripGroup);

            // Demo 4: Exact Split Expense
            demo4_ExactSplit(expenseService, userService, tripGroup);

            // Demo 5: Percentage Split Expense
            demo5_PercentageSplit(expenseService, userService, tripGroup);

            // Demo 6: View Balances
            demo6_ViewBalances(balanceManager);

            // Demo 7: Settle Payment
            demo7_SettlePayment(balanceManager, userService);

            // Demo 8: Group Summary
            demo8_GroupSummary(groupService, tripGroup);

            // Demo 9: User Expenses
            demo9_UserExpenses(expenseService, userService);

            // Demo 10: Debt Simplification (Greedy Algorithm)
            demo10_DebtSimplification(balanceManager);

            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                    DEMO COMPLETED                          ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demo1_CreateUsers(UserService userService) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 1: Creating Users                                  │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        userService.createUser("Alice Johnson", "alice@example.com", "1234567890");
        userService.createUser("Bob Smith", "bob@example.com", "0987654321");
        userService.createUser("Charlie Brown", "charlie@example.com", "1122334455");
        userService.createUser("Diana Prince", "diana@example.com", "5544332211");
    }

    private static Group demo2_CreateGroup(GroupService groupService, UserService userService) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 2: Creating Group                                  │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        User alice = userService.getUserByEmail("alice@example.com");
        User bob = userService.getUserByEmail("bob@example.com");
        User charlie = userService.getUserByEmail("charlie@example.com");

        return groupService.createGroup("Trip to Goa", Arrays.asList(alice, bob, charlie));
    }

    private static void demo3_EqualSplit(ExpenseService expenseService, UserService userService, Group group) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 3: Equal Split Expense                             │");
        System.out.println("│ Scenario: Alice paid 3000 for dinner, split equally     │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        User alice = userService.getUserByEmail("alice@example.com");
        User bob = userService.getUserByEmail("bob@example.com");
        User charlie = userService.getUserByEmail("charlie@example.com");

        // Using convenience method for better readability
        expenseService.createEqualExpense(
                "Dinner at Beach Restaurant",
                3000.0,
                alice,
                Arrays.asList(alice, bob, charlie),
                group
        );
    }

    private static void demo4_ExactSplit(ExpenseService expenseService, UserService userService, Group group) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 4: Exact Split Expense                             │");
        System.out.println("│ Scenario: Bob paid 5000 for hotel, custom amounts       │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        User alice = userService.getUserByEmail("alice@example.com");
        User bob = userService.getUserByEmail("bob@example.com");
        User charlie = userService.getUserByEmail("charlie@example.com");

        Map<User, Double> exactAmounts = new HashMap<>();
        exactAmounts.put(alice, 1500.0);
        exactAmounts.put(bob, 2000.0);
        exactAmounts.put(charlie, 1500.0);

        // Using convenience method for better readability
        expenseService.createExactExpense(
                "Hotel Booking",
                5000.0,
                bob,
                exactAmounts,
                group
        );
    }

    private static void demo5_PercentageSplit(ExpenseService expenseService, UserService userService, Group group) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 5: Percentage Split Expense                        │");
        System.out.println("│ Scenario: Charlie paid 4000 for activities, by %        │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        User alice = userService.getUserByEmail("alice@example.com");
        User bob = userService.getUserByEmail("bob@example.com");
        User charlie = userService.getUserByEmail("charlie@example.com");

        Map<User, Double> percentages = new HashMap<>();
        percentages.put(alice, 30.0);   // Alice 30%
        percentages.put(bob, 40.0);     // Bob 40%
        percentages.put(charlie, 30.0); // Charlie 30%

        // Using convenience method for better readability
        expenseService.createPercentageExpense(
                "Water Sports & Activities",
                4000.0,
                charlie,
                percentages,
                group
        );
    }

    private static void demo6_ViewBalances(BalanceManager balanceManager) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 6: Current Balances                                │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        balanceManager.printAllBalances();
    }

    private static void demo7_SettlePayment(BalanceManager balanceManager, UserService userService) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 7: Settle Payment                                  │");
        System.out.println("│ Scenario: Bob pays back 500 to Alice                    │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        User alice = userService.getUserByEmail("alice@example.com");
        User bob = userService.getUserByEmail("bob@example.com");

        balanceManager.recordPayment(bob, alice, 500.0);
        balanceManager.printAllBalances();
    }

    private static void demo8_GroupSummary(GroupService groupService, Group group) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 8: Group Summary                                   │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        groupService.printGroupSummary(group.getId());
    }

    private static void demo9_UserExpenses(ExpenseService expenseService, UserService userService) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 9: User Expenses                                   │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        User alice = userService.getUserByEmail("alice@example.com");
        List<Expense> aliceExpenses = expenseService.getExpensesByUser(alice);

        System.out.println("Expenses involving Alice: " + aliceExpenses.size());
        for (Expense expense : aliceExpenses) {
            System.out.println("  - " + expense.getDescription() + 
                             " (" + expense.getSplitType() + ")");
        }
        System.out.println();
    }

    private static void demo10_DebtSimplification(BalanceManager balanceManager) {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ DEMO 10: Debt Simplification (Greedy Algorithm)        │");
        System.out.println("│ Shows minimum transactions needed to settle all debts   │");
        System.out.println("└─────────────────────────────────────────────────────────┘");

        System.out.println("\nCurrent complex debt structure:");
        balanceManager.printAllBalances();

        System.out.println("Applying Greedy Algorithm for debt simplification...");
        balanceManager.printSimplifiedDebts();

        System.out.println("Explanation:");
        System.out.println("  The greedy algorithm reduces the number of transactions by:");
        System.out.println("  1. Calculating net balance for each user");
        System.out.println("  2. Matching largest creditor with largest debtor");
        System.out.println("  3. Settling as much as possible between them");
        System.out.println("  4. Repeating until all debts are cleared");
        System.out.println();
    }
}