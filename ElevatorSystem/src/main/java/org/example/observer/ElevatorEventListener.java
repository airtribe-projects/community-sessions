package org.example.observer;

import org.example.models.Elevator;

/**
 * Observer Pattern: Interface for elevator event listeners
 * Open/Closed Principle: Can add new listeners without modifying existing code
 */
public interface ElevatorEventListener {
    void onElevatorMoved(Elevator elevator, int fromFloor, int toFloor);
    void onElevatorDoorOpened(Elevator elevator);
    void onElevatorDoorClosed(Elevator elevator);
    void onElevatorStateChanged(Elevator elevator);
}
