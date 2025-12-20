package org.example.enums;

/**
 * Represents the current state of an elevator
 */
public enum ElevatorState {
    IDLE,           // Not moving, available for requests
    MOVING_UP,      // Moving upward
    MOVING_DOWN,    // Moving downward
    DOOR_OPEN,      // Stopped with door open
    MAINTENANCE     // Out of service
}
