package org.example.models;

/**
 * Represents a floor in the building
 * Composition: Floor HAS-A HallPanel
 */
public class Floor {
    private final int floorNumber;
    private final HallPanel hallPanel;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.hallPanel = new HallPanel(floorNumber);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public HallPanel getHallPanel() {
        return hallPanel;
    }

    @Override
    public String toString() {
        return "Floor " + floorNumber;
    }
}
