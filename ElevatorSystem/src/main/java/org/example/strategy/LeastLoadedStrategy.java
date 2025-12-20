package org.example.strategy;

import org.example.models.Elevator;
import org.example.models.Request;

import java.util.List;

/**
 * Strategy implementation: Selects elevator with least load
 * Useful for load balancing across elevators
 */
public class LeastLoadedStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator bestElevator = null;
        int minLoad = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (!elevator.isAvailable()) {
                continue;
            }

            int load = elevator.getCurrentLoad();
            
            // Consider distance as a secondary factor
            int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());
            int score = load * 10 + distance; // Weighted score

            if (score < minLoad) {
                minLoad = score;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }
}
