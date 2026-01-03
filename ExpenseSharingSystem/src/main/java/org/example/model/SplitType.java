package org.example.model;

/**
 * Enumeration of different split types supported by the system.
 * Can be extended to add new split types (e.g., SHARE_BASED, RATIO).
 */
public enum SplitType {
    EQUAL,      // Split equally among all participants
    EXACT,      // Each person pays exact specified amount
    PERCENTAGE  // Each person pays specified percentage
}
