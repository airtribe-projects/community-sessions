package org.example.models;

import org.example.enums.Direction;

/**
 * Represents the button panel on each floor (hall buttons)
 * Single Responsibility: Manages external floor buttons
 */
public class HallPanel {
    private final int floorNumber;
    private boolean upButtonPressed;
    private boolean downButtonPressed;

    public HallPanel(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upButtonPressed = false;
        this.downButtonPressed = false;
    }

    public void pressUpButton() {
        upButtonPressed = true;
        System.out.println("Floor " + floorNumber + " - UP button pressed");
    }

    public void pressDownButton() {
        downButtonPressed = true;
        System.out.println("Floor " + floorNumber + " - DOWN button pressed");
    }

    public void clearUpButton() {
        upButtonPressed = false;
    }

    public void clearDownButton() {
        downButtonPressed = false;
    }

    public void clearButton(Direction direction) {
        if (direction == Direction.UP) {
            clearUpButton();
        } else if (direction == Direction.DOWN) {
            clearDownButton();
        }
    }

    public boolean isUpButtonPressed() {
        return upButtonPressed;
    }

    public boolean isDownButtonPressed() {
        return downButtonPressed;
    }

    public int getFloorNumber() {
        return floorNumber;
    }
}
