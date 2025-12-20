package org.example.strategy;

import org.example.enums.Direction;
import org.example.models.Elevator;
import org.example.models.Request;

import java.util.List;

/**
 * Strategy implementation: Selects the nearest available elevator
 * KISS Principle: Simple and straightforward algorithm
 */
public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (!elevator.isAvailable()) {
                continue;
            }

            int distance = calculateDistance(elevator, request);
            
            // Prefer elevators moving in the same direction or idle elevators
            if (isElevatorSuitable(elevator, request)) {
                distance = distance / 2; // Give preference by reducing effective distance
            }

            if (distance < minDistance) {
                minDistance = distance;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }

    private int calculateDistance(Elevator elevator, Request request) {
        return Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());
    }

    private boolean isElevatorSuitable(Elevator elevator, Request request) {
        // Idle elevators are always suitable
        if (elevator.isIdle()) {
            return true;
        }

        Direction elevatorDirection = elevator.getCurrentDirection();
        Direction requestDirection = request.getDirection();
        int elevatorFloor = elevator.getCurrentFloor();
        int requestFloor = request.getSourceFloor();

        // Check if elevator is moving towards the request and in the same direction
        if (elevatorDirection == Direction.UP && requestDirection == Direction.UP) {
            return elevatorFloor <= requestFloor;
        } else if (elevatorDirection == Direction.DOWN && requestDirection == Direction.DOWN) {
            return elevatorFloor >= requestFloor;
        }

        return false;
    }
}
