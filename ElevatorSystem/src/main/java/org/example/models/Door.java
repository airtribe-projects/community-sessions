package org.example.models;

import org.example.enums.DoorState;

/**
 * Represents elevator door with state management
 * Single Responsibility: Manages only door operations
 */
public class Door {
    private DoorState state;

    public Door() {
        this.state = DoorState.CLOSED;
    }

    public void open() {
        if (state == DoorState.CLOSED) {
            state = DoorState.OPENING;
            System.out.println("Door is opening...");
            // Simulate door opening time
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            state = DoorState.OPEN;
            System.out.println("Door is now OPEN");
        }
    }

    public void close() {
        if (state == DoorState.OPEN) {
            state = DoorState.CLOSING;
            System.out.println("Door is closing...");
            // Simulate door closing time
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            state = DoorState.CLOSED;
            System.out.println("Door is now CLOSED");
        }
    }

    public DoorState getState() {
        return state;
    }

    public boolean isOpen() {
        return state == DoorState.OPEN;
    }

    public boolean isClosed() {
        return state == DoorState.CLOSED;
    }
}
