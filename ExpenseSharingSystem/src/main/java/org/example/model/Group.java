package org.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of users who share expenses.
 * Follows Composition over Inheritance - HAS-A list of users, not IS-A collection.
 * Follows SRP - Only manages group data and membership.
 */
public class Group {
    private final String id;
    private final String name;
    private final List<User> members;
    private final List<Expense> expenses;
    private final LocalDateTime createdAt;

    public Group(String id, String name, List<User> members) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>(members);
        this.expenses = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return new ArrayList<>(members);
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
        }
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public boolean isMember(User user) {
        return members.contains(user);
    }
}
