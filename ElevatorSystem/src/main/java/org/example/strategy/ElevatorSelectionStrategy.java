package org.example.strategy;

import org.example.models.Elevator;
import org.example.models.Request;

import java.util.List;

/**
 * Strategy Pattern: Interface for different elevator selection algorithms
 * Open/Closed Principle: Open for extension, closed for modification
 */
public interface ElevatorSelectionStrategy {
    /**
     * Selects the best elevator to serve the given request
     * @param elevators List of available elevators
     * @param request The request to be served
     * @return The selected elevator, or null if none available
     */
    Elevator selectElevator(List<Elevator> elevators, Request request);
}
