package org.example.service;

import org.example.model.Balance;
import org.example.model.Expense;
import org.example.model.Group;
import org.example.model.User;

import java.util.*;

/**
 * Service for managing group operations.
 * Follows SRP - Only handles group management.
 */
public class GroupService {
    private final List<Group> groups;
    private final ExpenseService expenseService;
    private int groupIdCounter;

    public GroupService(ExpenseService expenseService) {
        this.groups = new ArrayList<>();
        this.expenseService = expenseService;
        this.groupIdCounter = 1;
    }

    /**
     * Creates a new group.
     */
    public Group createGroup(String name, List<User> members) {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member");
        }

        String groupId = generateGroupId();
        Group group = new Group(groupId, name, members);
        groups.add(group);

        System.out.println("✓ Group created: " + group);
        return group;
    }

    /**
     * Retrieves a group by ID.
     */
    public Group getGroupById(String id) {
        return groups.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Group not found: " + id));
    }

    /**
     * Adds a member to a group.
     */
    public void addMemberToGroup(String groupId, User user) {
        Group group = getGroupById(groupId);
        group.addMember(user);
        System.out.println(String.format("✓ Added %s to group %s",
                user.getName(), group.getName()));
    }

    /**
     * Removes a member from a group.
     */
    public void removeMemberFromGroup(String groupId, User user) {
        Group group = getGroupById(groupId);
        group.removeMember(user);
        System.out.println(String.format("✓ Removed %s from group %s",
                user.getName(), group.getName()));
    }

    /**
     * Note: Adding expenses to groups is handled by ExpenseService.createExpense()
     * This keeps the responsibility in one place (SRP).
     * When creating an expense with a group, ExpenseService automatically adds it to the group.
     */

    /**
     * Gets all balances within a group.
     */
    public List<Balance> getGroupBalances(String groupId) {
        Group group = getGroupById(groupId);
        List<Expense> groupExpenses = expenseService.getExpensesByGroup(group);

        // Calculate balances from group expenses
        // This is a simplified version - full implementation would use BalanceManager
        List<Balance> balances = new ArrayList<>();
        return balances;
    }

    /**
     * Prints group summary.
     */
    public void printGroupSummary(String groupId) {
        Group group = getGroupById(groupId);
        List<Expense> groupExpenses = expenseService.getExpensesByGroup(group);

        System.out.println("\n=== Group Summary: " + group.getName() + " ===");
        System.out.println("Members: " + group.getMembers().size());
        for (User member : group.getMembers()) {
            System.out.println("  - " + member.getName());
        }
        System.out.println("Total Expenses: " + groupExpenses.size());
        System.out.println();
    }

    private String generateGroupId() {
        return "G" + String.format("%04d", groupIdCounter++);
    }
}
