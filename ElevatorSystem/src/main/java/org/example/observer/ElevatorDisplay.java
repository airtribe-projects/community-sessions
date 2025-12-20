package org.example.observer;

import org.example.models.Elevator;

/**
 * Concrete Observer: Displays elevator status updates
 * Single Responsibility: Only handles display logic
 */
public class ElevatorDisplay implements ElevatorEventListener {
    private final String displayName;

    public ElevatorDisplay(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void onElevatorMoved(Elevator elevator, int fromFloor, int toFloor) {
        System.out.println("[" + displayName + "] Elevator " + elevator.getId() + 
                         " moved from floor " + fromFloor + " to floor " + toFloor);
    }

    @Override
    public void onElevatorDoorOpened(Elevator elevator) {
        System.out.println("[" + displayName + "] Elevator " + elevator.getId() + 
                         " door opened at floor " + elevator.getCurrentFloor());
    }

    @Override
    public void onElevatorDoorClosed(Elevator elevator) {
        System.out.println("[" + displayName + "] Elevator " + elevator.getId() + 
                         " door closed at floor " + elevator.getCurrentFloor());
    }

    @Override
    public void onElevatorStateChanged(Elevator elevator) {
        System.out.println("[" + displayName + "] Elevator " + elevator.getId() + 
                         " state changed to " + elevator.getState());
    }
}
