package org.example.controller;

import org.example.models.Elevator;
import org.example.models.Request;
import org.example.enums.RequestType;

/**
 * Controller for individual elevator operations
 * Single Responsibility: Controls one elevator's behavior
 */
public class ElevatorController implements Runnable {
    private final Elevator elevator;
    private volatile boolean running;

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Elevator Controller " + elevator.getId() + " started");
        
        while (running) {
            try {
                processNextDestination();
                Thread.sleep(500); // Small delay between checks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processNextDestination() {
        Integer nextFloor = elevator.getNextDestination();
        
        if (nextFloor != null) {
            elevator.moveToFloor(nextFloor);
        }
    }

    public void handleRequest(Request request) {
        if (request.getRequestType() == RequestType.HALL_REQUEST) {
            // For hall requests, first go to the source floor
            elevator.addDestination(request.getSourceFloor());
        } else if (request.getRequestType() == RequestType.ELEVATOR_REQUEST) {
            // For elevator requests, go directly to destination
            if (request.getDestinationFloor() != null) {
                elevator.addDestination(request.getDestinationFloor());
            }
        }
    }

    public Elevator getElevator() {
        return elevator;
    }

    public void stop() {
        running = false;
    }
}
