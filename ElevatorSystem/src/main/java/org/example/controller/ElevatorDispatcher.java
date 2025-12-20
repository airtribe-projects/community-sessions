package org.example.controller;

import org.example.models.Elevator;
import org.example.models.Request;
import org.example.strategy.ElevatorSelectionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Dispatcher that assigns requests to appropriate elevators
 * Single Responsibility: Manages request distribution
 * Dependency Inversion: Depends on abstraction (ElevatorSelectionStrategy)
 */
public class ElevatorDispatcher implements Runnable {
    private final List<ElevatorController> elevatorControllers;
    private final BlockingQueue<Request> requestQueue;
    private final ElevatorSelectionStrategy selectionStrategy;
    private volatile boolean running;

    public ElevatorDispatcher(List<ElevatorController> elevatorControllers, 
                             ElevatorSelectionStrategy selectionStrategy) {
        this.elevatorControllers = elevatorControllers;
        this.requestQueue = new LinkedBlockingQueue<>();
        this.selectionStrategy = selectionStrategy;
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Elevator Dispatcher started");
        
        while (running) {
            try {
                Request request = requestQueue.take(); // Blocking call
                dispatchRequest(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void dispatchRequest(Request request) {
        System.out.println("\n=== Dispatching Request ===");
        System.out.println(request);

        List<Elevator> elevators = new ArrayList<>();
        for (ElevatorController controller : elevatorControllers) {
            elevators.add(controller.getElevator());
        }

        Elevator selectedElevator = selectionStrategy.selectElevator(elevators, request);

        if (selectedElevator != null) {
            System.out.println("Selected Elevator " + selectedElevator.getId() + " for request");
            
            // Find the controller for this elevator
            for (ElevatorController controller : elevatorControllers) {
                if (controller.getElevator().getId() == selectedElevator.getId()) {
                    controller.handleRequest(request);
                    break;
                }
            }
        } else {
            System.out.println("No available elevator found for request");
        }
    }

    public void submitRequest(Request request) {
        try {
            requestQueue.put(request);
            System.out.println("Request queued: " + request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        running = false;
    }
}
