# Elevator System - Detailed Design Document

## Table of Contents
1. [System Overview](#system-overview)
2. [Design Principles](#design-principles)
3. [Component Details](#component-details)
4. [Design Patterns](#design-patterns)
5. [Algorithms](#algorithms)
6. [Extensibility](#extensibility)

---

## System Overview

The Elevator System is a multi-threaded application that manages multiple elevators serving multiple floors in a building. It handles concurrent user requests efficiently while optimizing elevator movement.

### Key Features
- Multiple elevators operating independently
- Concurrent request handling
- Pluggable elevator selection strategies
- Real-time state management
- Thread-safe operations
- Modular and extensible architecture

---

## Design Principles

### 1. SOLID Principles

#### Single Responsibility Principle (SRP)
Each class has one reason to change:

- **Door**: Manages door state transitions only
  ```java
  public class Door {
      private DoorState state;
      public void open() { /* door logic */ }
      public void close() { /* door logic */ }
  }
  ```

- **ElevatorPanel**: Manages internal buttons only
- **HallPanel**: Manages external buttons only
- **ElevatorController**: Controls one elevator's operations
- **ElevatorDispatcher**: Distributes requests to elevators

#### Open/Closed Principle (OCP)
Open for extension, closed for modification:

```java
// Interface for strategy
public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

// Can add new strategies without modifying existing code
public class NearestElevatorStrategy implements ElevatorSelectionStrategy { }
public class LeastLoadedStrategy implements ElevatorSelectionStrategy { }
public class ZoneBasedStrategy implements ElevatorSelectionStrategy { } // New!
```

#### Liskov Substitution Principle (LSP)
Any strategy implementation can replace another:

```java
// Both work seamlessly
Building building1 = new Building.Builder()
    .selectionStrategy(new NearestElevatorStrategy())
    .build();

Building building2 = new Building.Builder()
    .selectionStrategy(new LeastLoadedStrategy())
    .build();
```

#### Interface Segregation Principle (ISP)
Focused, specific interfaces:

```java
// Clients only depend on methods they use
public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

public interface ElevatorEventListener {
    void onElevatorMoved(Elevator elevator, int fromFloor, int toFloor);
    void onElevatorDoorOpened(Elevator elevator);
    void onElevatorDoorClosed(Elevator elevator);
    void onElevatorStateChanged(Elevator elevator);
}
```

#### Dependency Inversion Principle (DIP)
Depend on abstractions, not concretions:

```java
public class ElevatorDispatcher {
    // Depends on abstraction, not concrete implementation
    private final ElevatorSelectionStrategy selectionStrategy;
    
    public ElevatorDispatcher(List<ElevatorController> controllers, 
                             ElevatorSelectionStrategy strategy) {
        this.selectionStrategy = strategy;
    }
}
```

### 2. DRY (Don't Repeat Yourself)
- Reusable components: `Door`, `ElevatorPanel`, `HallPanel`
- Common request handling centralized in `ElevatorController`
- Shared state management logic in `Elevator`

### 3. KISS (Keep It Simple, Stupid)
- Straightforward implementations
- Clear method names
- Simple algorithms
- No unnecessary complexity

### 4. YAGNI (You Aren't Gonna Need It)
- Only implemented required features
- No speculative generality
- Focused on core functionality

### 5. Composition over Inheritance
```
Building HAS-A List<Elevator>
Building HAS-A List<Floor>
Elevator HAS-A Door
Elevator HAS-A ElevatorPanel
Floor HAS-A HallPanel
```

No deep inheritance hierarchies - using composition for flexibility.

### 6. Encapsulation
- Private fields with controlled access
- Immutable objects where appropriate (Request)
- Defensive copying in getters
- State changes through well-defined methods

---

## Component Details

### 1. Enums

#### Direction
```java
public enum Direction {
    UP,      // Elevator moving upward
    DOWN,    // Elevator moving downward
    IDLE     // Elevator not moving
}
```

#### ElevatorState
```java
public enum ElevatorState {
    IDLE,           // Available for requests
    MOVING_UP,      // Moving upward
    MOVING_DOWN,    // Moving downward
    DOOR_OPEN,      // Stopped with door open
    MAINTENANCE     // Out of service
}
```

#### DoorState
```java
public enum DoorState {
    OPEN,      // Door fully open
    CLOSED,    // Door fully closed
    OPENING,   // Door in process of opening
    CLOSING    // Door in process of closing
}
```

#### RequestType
```java
public enum RequestType {
    HALL_REQUEST,       // From floor hall button
    ELEVATOR_REQUEST    // From inside elevator
}
```

### 2. Core Models

#### Request
**Purpose**: Represents an elevator service request

**Design Decisions**:
- Immutable (thread-safe)
- Builder pattern for flexible construction
- Timestamp for request ordering

```java
Request request = new Request.Builder()
    .sourceFloor(3)
    .destinationFloor(7)
    .direction(Direction.UP)
    .requestType(RequestType.HALL_REQUEST)
    .build();
```

#### Door
**Purpose**: Manages elevator door operations

**Design Decisions**:
- Single responsibility: only door logic
- State machine for transitions
- Simulates realistic timing

**State Transitions**:
```
CLOSED → OPENING → OPEN → CLOSING → CLOSED
```

#### ElevatorPanel
**Purpose**: Internal button panel inside elevator

**Design Decisions**:
- Uses Set for unique floor selections
- Prevents duplicate button presses
- Provides defensive copying

#### HallPanel
**Purpose**: External button panel on each floor

**Design Decisions**:
- Separate UP and DOWN buttons
- Floor-specific identification
- Simple boolean state

#### Floor
**Purpose**: Represents a building floor

**Design Decisions**:
- Composition: HAS-A HallPanel
- Immutable floor number
- Simple, focused responsibility

#### Elevator
**Purpose**: The elevator car with full state management

**Design Decisions**:
- Composition: HAS-A Door and ElevatorPanel
- TreeSet for automatic destination sorting
- Separate up and down destination queues
- Thread-safe state management

**Key Features**:
- Automatic destination sorting (TreeSet)
- Efficient stop determination
- Direction-aware movement
- Capacity management

**Destination Management**:
```java
private final TreeSet<Integer> upDestinations;
private final TreeSet<Integer> downDestinations;

// Automatically sorted for efficient traversal
upDestinations.add(5);    // [5]
upDestinations.add(3);    // [3, 5]
upDestinations.add(7);    // [3, 5, 7]
```

#### Building
**Purpose**: Facade for the entire elevator system

**Design Decisions**:
- Facade pattern: simplified interface
- Builder pattern: flexible construction
- Manages all system components
- Coordinates threads

**Public API**:
```java
building.requestElevatorUp(floor);
building.requestElevatorDown(floor);
building.selectFloorInElevator(elevatorId, floor);
building.start();
building.stop();
```

### 3. Controllers

#### ElevatorController
**Purpose**: Controls individual elevator operations

**Design Decisions**:
- One controller per elevator
- Runnable for concurrent execution
- Processes destinations continuously
- Handles both hall and elevator requests

**Thread Model**:
```java
public void run() {
    while (running) {
        processNextDestination();
        Thread.sleep(500);
    }
}
```

#### ElevatorDispatcher
**Purpose**: Distributes requests to optimal elevators

**Design Decisions**:
- BlockingQueue for thread-safe request handling
- Strategy pattern for selection algorithm
- Runnable for concurrent execution
- Decoupled from selection logic

**Request Flow**:
```
Request → Queue → Dispatcher → Strategy → Controller → Elevator
```

### 4. Strategies

#### ElevatorSelectionStrategy (Interface)
**Purpose**: Define contract for elevator selection

**Design Decisions**:
- Strategy pattern for algorithm flexibility
- Single method interface (ISP)
- Returns null if no elevator available

#### NearestElevatorStrategy
**Purpose**: Select closest available elevator

**Algorithm**:
1. Calculate distance for each elevator
2. Prefer elevators moving in same direction
3. Prefer idle elevators
4. Select minimum distance

**Optimization**:
```java
// Reduce effective distance for suitable elevators
if (isElevatorSuitable(elevator, request)) {
    distance = distance / 2;
}
```

#### LeastLoadedStrategy
**Purpose**: Balance load across elevators

**Algorithm**:
1. Calculate load for each elevator
2. Consider distance as secondary factor
3. Use weighted score: `load * 10 + distance`
4. Select minimum score

### 5. Observers

#### ElevatorEventListener (Interface)
**Purpose**: Define contract for event notifications

**Design Decisions**:
- Observer pattern for loose coupling
- Multiple event types
- Can have multiple listeners

#### ElevatorDisplay
**Purpose**: Display elevator status updates

**Design Decisions**:
- Concrete observer implementation
- Simple console output
- Can be extended for GUI, logging, etc.

---

## Design Patterns

### 1. Strategy Pattern

**Problem**: Need different algorithms for elevator selection

**Solution**: 
```java
public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}
```

**Benefits**:
- Easy to add new algorithms
- Runtime algorithm switching
- Testable in isolation
- Follows OCP

**Usage**:
```java
Building building = new Building.Builder()
    .selectionStrategy(new NearestElevatorStrategy())
    .build();
```

### 2. Builder Pattern

**Problem**: Complex object construction with many parameters

**Solution**:
```java
Building building = new Building.Builder()
    .name("Tech Tower")
    .floors(10)
    .elevators(3, 10)
    .selectionStrategy(new NearestElevatorStrategy())
    .build();
```

**Benefits**:
- Readable code
- Optional parameters
- Immutable objects
- Validation at build time

### 3. Observer Pattern

**Problem**: Need to notify multiple components of elevator events

**Solution**:
```java
public interface ElevatorEventListener {
    void onElevatorMoved(Elevator elevator, int from, int to);
    void onElevatorDoorOpened(Elevator elevator);
    // ... other events
}
```

**Benefits**:
- Loose coupling
- Multiple observers
- Easy to add new observers
- Follows OCP

### 4. Facade Pattern

**Problem**: Complex subsystem with many components

**Solution**: Building class provides simple interface
```java
building.requestElevatorUp(3);
building.selectFloorInElevator(1, 7);
```

**Benefits**:
- Hide complexity
- Simple API
- Decoupled clients
- Easy to use

---

## Algorithms

### 1. Elevator Movement Algorithm

```
1. Check if there are destinations
2. Get next destination based on current direction
3. Move towards destination floor by floor
4. At each floor:
   - Check if should stop
   - If yes: open door, wait, close door
   - Remove from destination set
5. When no more destinations in current direction:
   - Switch direction if destinations exist
   - Otherwise, become IDLE
```

### 2. Nearest Elevator Selection

```
1. For each available elevator:
   - Calculate distance to request floor
   - If elevator is suitable (moving in same direction):
     - Reduce effective distance by 50%
   - Track minimum distance
2. Return elevator with minimum distance
```

### 3. Destination Management

```
- Use TreeSet for automatic sorting
- Separate sets for UP and DOWN directions
- When moving UP: take first (lowest) from upDestinations
- When moving DOWN: take last (highest) from downDestinations
- Efficient O(log n) insertion and removal
```

---

## Extensibility

### Adding New Features

#### 1. Priority Requests
```java
public class PriorityRequest extends Request {
    private final int priority;
    // Higher priority requests served first
}
```

#### 2. Access Control
```java
public class SecureFloor extends Floor {
    private final Set<String> authorizedUsers;
    
    public boolean canAccess(String userId) {
        return authorizedUsers.contains(userId);
    }
}
```

#### 3. Energy Optimization
```java
public class EnergyEfficientStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        // Minimize total energy consumption
        // Consider: distance, current load, direction changes
    }
}
```

#### 4. Maintenance Scheduling
```java
public class MaintenanceScheduler {
    public void scheduleMaintenanceFor(Elevator elevator, LocalDateTime time) {
        // Set elevator to MAINTENANCE state at specified time
        // Redistribute pending requests
    }
}
```

#### 5. Real-time Monitoring
```java
public class MonitoringDisplay implements ElevatorEventListener {
    @Override
    public void onElevatorMoved(Elevator elevator, int from, int to) {
        // Update dashboard
        // Send metrics to monitoring system
        // Alert if anomalies detected
    }
}
```

### Scalability Considerations

#### Horizontal Scaling
- Multiple buildings with separate systems
- Distributed request queue (Kafka, RabbitMQ)
- Centralized monitoring

#### Vertical Scaling
- More elevators per building
- More floors
- Higher request throughput

#### Performance Optimization
- Connection pooling for database
- Caching frequently accessed data
- Async processing for non-critical operations

---

## Testing Strategy

### Unit Tests
```java
@Test
public void testNearestElevatorStrategy() {
    // Test strategy in isolation
}

@Test
public void testElevatorMovement() {
    // Test elevator state transitions
}

@Test
public void testRequestBuilder() {
    // Test request construction
}
```

### Integration Tests
```java
@Test
public void testMultipleElevatorsHandlingRequests() {
    // Test full system integration
}
```

### Concurrency Tests
```java
@Test
public void testConcurrentRequests() {
    // Test thread safety
}
```

---

## Conclusion

This elevator system demonstrates:
- ✅ Clean architecture
- ✅ SOLID principles
- ✅ Design patterns
- ✅ Modular design
- ✅ Extensibility
- ✅ Thread safety
- ✅ Scalability
- ✅ Maintainability

The system is production-ready and can be extended with additional features as needed.
