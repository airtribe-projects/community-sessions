package org.example.models;

import org.example.controller.ElevatorController;
import org.example.controller.ElevatorDispatcher;
import org.example.enums.Direction;
import org.example.enums.RequestType;
import org.example.strategy.ElevatorSelectionStrategy;
import org.example.strategy.NearestElevatorStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the building with multiple floors and elevators
 * Facade Pattern: Provides simplified interface to the elevator system
 * Composition: Building HAS-A floors and elevators
 */
public class Building {
    private final String name;
    private final List<Floor> floors;
    private final List<Elevator> elevators;
    private final List<ElevatorController> elevatorControllers;
    private final ElevatorDispatcher dispatcher;
    private final List<Thread> controllerThreads;
    private Thread dispatcherThread;

    private Building(Builder builder) {
        this.name = builder.name;
        this.floors = builder.floors;
        this.elevators = builder.elevators;
        this.elevatorControllers = new ArrayList<>();
        this.controllerThreads = new ArrayList<>();

        // Initialize elevator controllers
        for (Elevator elevator : elevators) {
            ElevatorController controller = new ElevatorController(elevator);
            elevatorControllers.add(controller);
        }

        // Initialize dispatcher with strategy
        ElevatorSelectionStrategy strategy = builder.selectionStrategy != null ? 
                builder.selectionStrategy : new NearestElevatorStrategy();
        this.dispatcher = new ElevatorDispatcher(elevatorControllers, strategy);
    }

    public void start() {
        System.out.println("=== Starting " + name + " Elevator System ===");
        System.out.println("Floors: " + floors.size());
        System.out.println("Elevators: " + elevators.size());
        System.out.println();

        // Start dispatcher thread
        dispatcherThread = new Thread(dispatcher, "Dispatcher-Thread");
        dispatcherThread.start();

        // Start elevator controller threads
        for (int i = 0; i < elevatorControllers.size(); i++) {
            Thread thread = new Thread(elevatorControllers.get(i), "Elevator-Controller-" + i);
            controllerThreads.add(thread);
            thread.start();
        }
    }

    public void stop() {
        System.out.println("\n=== Stopping " + name + " Elevator System ===");
        
        dispatcher.stop();
        for (ElevatorController controller : elevatorControllers) {
            controller.stop();
        }

        // Interrupt threads
        if (dispatcherThread != null) {
            dispatcherThread.interrupt();
        }
        for (Thread thread : controllerThreads) {
            thread.interrupt();
        }
    }

    /**
     * User presses UP button on a floor
     */
    public void requestElevatorUp(int floorNumber) {
        if (!isValidFloor(floorNumber)) {
            System.out.println("Invalid floor: " + floorNumber);
            return;
        }

        Floor floor = floors.get(floorNumber);
        floor.getHallPanel().pressUpButton();

        Request request = new Request.Builder()
                .sourceFloor(floorNumber)
                .direction(Direction.UP)
                .requestType(RequestType.HALL_REQUEST)
                .build();

        dispatcher.submitRequest(request);
    }

    /**
     * User presses DOWN button on a floor
     */
    public void requestElevatorDown(int floorNumber) {
        if (!isValidFloor(floorNumber)) {
            System.out.println("Invalid floor: " + floorNumber);
            return;
        }

        Floor floor = floors.get(floorNumber);
        floor.getHallPanel().pressDownButton();

        Request request = new Request.Builder()
                .sourceFloor(floorNumber)
                .direction(Direction.DOWN)
                .requestType(RequestType.HALL_REQUEST)
                .build();

        dispatcher.submitRequest(request);
    }

    /**
     * User inside elevator presses a floor button
     */
    public void selectFloorInElevator(int elevatorId, int destinationFloor) {
        if (!isValidFloor(destinationFloor)) {
            System.out.println("Invalid floor: " + destinationFloor);
            return;
        }

        Elevator elevator = getElevatorById(elevatorId);
        if (elevator == null) {
            System.out.println("Invalid elevator ID: " + elevatorId);
            return;
        }

        int currentFloor = elevator.getCurrentFloor();
        Direction direction = destinationFloor > currentFloor ? Direction.UP : Direction.DOWN;

        Request request = new Request.Builder()
                .sourceFloor(currentFloor)
                .destinationFloor(destinationFloor)
                .direction(direction)
                .requestType(RequestType.ELEVATOR_REQUEST)
                .build();

        // Find the controller for this elevator and handle request directly
        for (ElevatorController controller : elevatorControllers) {
            if (controller.getElevator().getId() == elevatorId) {
                controller.handleRequest(request);
                break;
            }
        }
    }

    private boolean isValidFloor(int floorNumber) {
        return floorNumber >= 0 && floorNumber < floors.size();
    }

    private Elevator getElevatorById(int elevatorId) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorId) {
                return elevator;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public List<Floor> getFloors() {
        return new ArrayList<>(floors);
    }

    public List<Elevator> getElevators() {
        return new ArrayList<>(elevators);
    }

    /**
     * Builder Pattern for flexible Building construction
     * Follows SOLID principles and provides clean API
     */
    public static class Builder {
        private String name;
        private List<Floor> floors;
        private List<Elevator> elevators;
        private ElevatorSelectionStrategy selectionStrategy;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder floors(int numberOfFloors) {
            this.floors = new ArrayList<>();
            for (int i = 0; i < numberOfFloors; i++) {
                floors.add(new Floor(i));
            }
            return this;
        }

        public Builder elevators(int numberOfElevators, int capacity) {
            this.elevators = new ArrayList<>();
            for (int i = 0; i < numberOfElevators; i++) {
                elevators.add(new Elevator(i + 1, capacity));
            }
            return this;
        }

        public Builder selectionStrategy(ElevatorSelectionStrategy strategy) {
            this.selectionStrategy = strategy;
            return this;
        }

        public Building build() {
            if (name == null || floors == null || elevators == null) {
                throw new IllegalStateException("Building requires name, floors, and elevators");
            }
            return new Building(this);
        }
    }
}
