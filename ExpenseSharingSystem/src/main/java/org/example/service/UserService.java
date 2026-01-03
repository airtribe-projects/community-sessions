package org.example.service;

import org.example.model.User;

import java.util.*;

/**
 * Service for managing user operations.
 * Follows SRP - Only handles user management.
 */
public class UserService {
    private final Map<String, User> users;
    private final Map<String, User> emailIndex;
    private int userIdCounter;

    public UserService() {
        this.users = new HashMap<>();
        this.emailIndex = new HashMap<>();
        this.userIdCounter = 1;
    }

    /**
     * Creates a new user in the system.
     */
    public User createUser(String name, String email, String phone) {
        if (emailIndex.containsKey(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        String userId = generateUserId();
        User user = new User(userId, name, email, phone);
        users.put(userId, user);
        emailIndex.put(email, user);

        System.out.println("✓ User created: " + user);
        return user;
    }

    /**
     * Retrieves a user by ID.
     */
    public User getUserById(String id) {
        User user = users.get(id);
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return user;
    }

    /**
     * Retrieves a user by email.
     */
    public User getUserByEmail(String email) {
        User user = emailIndex.get(email);
        if (user == null) {
            throw new NoSuchElementException("User not found with email: " + email);
        }
        return user;
    }

    /**
     * Gets all users in the system.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private String generateUserId() {
        return "U" + String.format("%04d", userIdCounter++);
    }
}
