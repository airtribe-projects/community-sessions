package org.example.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the button panel inside an elevator
 * Single Responsibility: Manages internal elevator buttons
 */
public class ElevatorPanel {
    private final int elevatorId;
    private final Set<Integer> pressedButtons;

    public ElevatorPanel(int elevatorId) {
        this.elevatorId = elevatorId;
        this.pressedButtons = new HashSet<>();
    }

    public void pressButton(int floor) {
        pressedButtons.add(floor);
        System.out.println("Elevator " + elevatorId + " - Button pressed for floor: " + floor);
    }

    public void clearButton(int floor) {
        pressedButtons.remove(floor);
    }

    public Set<Integer> getPressedButtons() {
        return new HashSet<>(pressedButtons); // Return copy to maintain encapsulation
    }

    public boolean hasDestination(int floor) {
        return pressedButtons.contains(floor);
    }

    public void clearAllButtons() {
        pressedButtons.clear();
    }
}
