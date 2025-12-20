package org.example;

import org.example.models.Building;
import org.example.strategy.NearestElevatorStrategy;

/**
 * Main application demonstrating the Elevator System
 * 
 * Design Principles Applied:
 * - SOLID: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
 * - DRY: Don't Repeat Yourself - reusable components
 * - KISS: Keep It Simple, Stupid - straightforward implementations
 * - YAGNI: You Aren't Gonna Need It - only necessary features
 * - Composition over Inheritance: Building HAS-A elevators, Elevator HAS-A door
 * - Encapsulation: Private fields with controlled access
 * 
 * Design Patterns Used:
 * - Strategy Pattern: ElevatorSelectionStrategy for different algorithms
 * - Builder Pattern: Building and Request construction
 * - Observer Pattern: ElevatorEventListener for notifications
 * - Facade Pattern: Building provides simplified interface
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         ELEVATOR SYSTEM - LOW LEVEL DESIGN DEMO           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // Create a building with 10 floors and 3 elevators
        Building building = new Building.Builder()
                .name("Tech Tower")
                .floors(10)
                .elevators(3, 10) // 3 elevators with capacity of 10 people each
                .selectionStrategy(new NearestElevatorStrategy())
                .build();

        // Start the elevator system
        building.start();

        try {
            // Simulate various scenarios
            runScenarios(building);

            // Let the system process requests
            Thread.sleep(30000); // Wait 30 seconds for all operations to complete

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted");
        } finally {
            // Shutdown the system
            building.stop();
            System.out.println("\nSystem shutdown complete.");
        }
    }

    private static void runScenarios(Building building) throws InterruptedException {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 1: User on Floor 3 wants to go UP");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        building.requestElevatorUp(3);
        Thread.sleep(3000);
        
        // Once elevator arrives, user selects floor 7
        System.out.println("\n[User enters elevator and selects floor 7]");
        building.selectFloorInElevator(1, 7);
        Thread.sleep(5000);

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 2: Multiple users on different floors");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        building.requestElevatorDown(8);
        Thread.sleep(500);
        building.requestElevatorUp(2);
        Thread.sleep(500);
        building.requestElevatorUp(5);
        Thread.sleep(8000);

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 3: User inside elevator selects multiple floors");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        building.selectFloorInElevator(2, 4);
        Thread.sleep(500);
        building.selectFloorInElevator(2, 6);
        Thread.sleep(500);
        building.selectFloorInElevator(2, 9);
        Thread.sleep(8000);

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCENARIO 4: Peak hour simulation - many requests");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        for (int i = 1; i <= 6; i++) {
            building.requestElevatorUp(i);
            Thread.sleep(300);
        }
    }
}