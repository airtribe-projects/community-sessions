package org.example.models;

import org.example.enums.Direction;
import org.example.enums.ElevatorState;

import java.util.TreeSet;

/**
 * Represents an elevator car
 * Composition: Elevator HAS-A Door and HAS-A ElevatorPanel
 * Encapsulates what changes: elevator state, position, and destinations
 */
public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction currentDirection;
    private ElevatorState state;
    private final Door door;
    private final ElevatorPanel panel;
    private final int capacity;
    private int currentLoad;
    
    // Using TreeSet for automatic sorting of destination floors
    private final TreeSet<Integer> upDestinations;
    private final TreeSet<Integer> downDestinations;

    public Elevator(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.currentFloor = 0; // Start at ground floor
        this.currentDirection = Direction.IDLE;
        this.state = ElevatorState.IDLE;
        this.door = new Door();
        this.panel = new ElevatorPanel(id);
        this.currentLoad = 0;
        this.upDestinations = new TreeSet<>();
        this.downDestinations = new TreeSet<>();
    }

    public void addDestination(int floor) {
        if (floor > currentFloor) {
            upDestinations.add(floor);
        } else if (floor < currentFloor) {
            downDestinations.add(floor);
        }
        panel.pressButton(floor);
    }

    public void moveToFloor(int targetFloor) {
        if (currentFloor == targetFloor) {
            return;
        }

        if (targetFloor > currentFloor) {
            currentDirection = Direction.UP;
            state = ElevatorState.MOVING_UP;
        } else {
            currentDirection = Direction.DOWN;
            state = ElevatorState.MOVING_DOWN;
        }

        System.out.println("Elevator " + id + " moving from floor " + currentFloor + " to floor " + targetFloor);

        // Simulate movement
        while (currentFloor != targetFloor) {
            try {
                Thread.sleep(1000); // Simulate time to move one floor
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (currentDirection == Direction.UP) {
                currentFloor++;
            } else {
                currentFloor--;
            }

            System.out.println("Elevator " + id + " at floor " + currentFloor);

            // Check if we need to stop at this floor
            if (shouldStopAtFloor(currentFloor)) {
                stopAtFloor(currentFloor);
            }
        }
    }

    private boolean shouldStopAtFloor(int floor) {
        if (currentDirection == Direction.UP && upDestinations.contains(floor)) {
            return true;
        }
        if (currentDirection == Direction.DOWN && downDestinations.contains(floor)) {
            return true;
        }
        return false;
    }

    private void stopAtFloor(int floor) {
        state = ElevatorState.DOOR_OPEN;
        System.out.println("Elevator " + id + " stopping at floor " + floor);
        
        door.open();
        
        // Remove this floor from destinations
        upDestinations.remove(floor);
        downDestinations.remove(floor);
        panel.clearButton(floor);
        
        // Simulate passenger boarding/alighting
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        door.close();
    }

    public Integer getNextDestination() {
        if (currentDirection == Direction.UP && !upDestinations.isEmpty()) {
            return upDestinations.first();
        } else if (currentDirection == Direction.DOWN && !downDestinations.isEmpty()) {
            return downDestinations.last();
        } else if (!upDestinations.isEmpty()) {
            currentDirection = Direction.UP;
            return upDestinations.first();
        } else if (!downDestinations.isEmpty()) {
            currentDirection = Direction.DOWN;
            return downDestinations.last();
        }
        
        currentDirection = Direction.IDLE;
        state = ElevatorState.IDLE;
        return null;
    }

    public boolean hasDestinations() {
        return !upDestinations.isEmpty() || !downDestinations.isEmpty();
    }

    public boolean isAvailable() {
        return state != ElevatorState.MAINTENANCE;
    }

    public boolean isIdle() {
        return state == ElevatorState.IDLE;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public ElevatorState getState() {
        return state;
    }

    public Door getDoor() {
        return door;
    }

    public ElevatorPanel getPanel() {
        return panel;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = Math.min(currentLoad, capacity);
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Elevator{" +
                "id=" + id +
                ", floor=" + currentFloor +
                ", direction=" + currentDirection +
                ", state=" + state +
                '}';
    }
}
