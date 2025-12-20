# Elevator System - Low Level Design

A comprehensive elevator system implementation demonstrating SOLID principles, design patterns, and clean architecture.

## 🏗️ Architecture Overview

This elevator system is designed to manage multiple elevators in a multi-floor building with a focus on modularity, extensibility, and maintainability.

## 📦 Core Entities

### 1. **Building**
- Manages multiple elevators and floors
- Provides facade interface for user interactions
- Coordinates elevator controllers and dispatcher

### 2. **Floor**
- Represents a floor in the building
- Contains HallPanel for external buttons
- Composition: Floor HAS-A HallPanel

### 3. **Elevator**
- Represents an elevator car
- Manages current position, direction, and state
- Composition: Elevator HAS-A Door and ElevatorPanel
- Uses TreeSet for efficient destination management

### 4. **Door**
- Manages door states (OPEN, CLOSED, OPENING, CLOSING)
- Single responsibility: Only door operations

### 5. **ElevatorPanel**
- Internal button panel inside elevator
- Tracks pressed floor buttons

### 6. **HallPanel**
- External button panel on each floor
- UP and DOWN buttons for calling elevators

### 7. **Request**
- Represents elevator service requests
- Types: HALL_REQUEST (external) and ELEVATOR_REQUEST (internal)
- Built using Builder pattern

## 🎯 Design Principles Applied

### SOLID Principles

#### Single Responsibility Principle (SRP)
- `Door` - Only manages door operations
- `ElevatorPanel` - Only manages internal buttons
- `HallPanel` - Only manages hall buttons
- `ElevatorController` - Controls one elevator
- `ElevatorDispatcher` - Dispatches requests

#### Open/Closed Principle (OCP)
- `ElevatorSelectionStrategy` interface allows new algorithms without modifying existing code
- Can add new strategies like `LeastLoadedStrategy`, `ZoneBasedStrategy` without changing dispatcher

#### Liskov Substitution Principle (LSP)
- Any `ElevatorSelectionStrategy` implementation can be substituted
- `NearestElevatorStrategy` and `LeastLoadedStrategy` are interchangeable

#### Interface Segregation Principle (ISP)
- `ElevatorSelectionStrategy` - Focused interface for selection logic
- `ElevatorEventListener` - Specific interface for event handling

#### Dependency Inversion Principle (DIP)
- `ElevatorDispatcher` depends on `ElevatorSelectionStrategy` abstraction, not concrete implementations
- High-level modules don't depend on low-level modules

### Other Principles

#### DRY (Don't Repeat Yourself)
- Reusable components: `Door`, `ElevatorPanel`, `HallPanel`
- Common request handling logic centralized

#### KISS (Keep It Simple, Stupid)
- Straightforward implementations
- Clear, readable code
- Simple algorithms

#### YAGNI (You Aren't Gonna Need It)
- Only implemented necessary features
- No over-engineering
- Focused on core functionality

#### Composition over Inheritance
- `Building` HAS-A elevators and floors
- `Elevator` HAS-A door and panel
- `Floor` HAS-A hall panel
- No deep inheritance hierarchies

#### Encapsulation
- Private fields with controlled access
- Immutable where appropriate (Request)
- Defensive copying in getters

## 🎨 Design Patterns Used

### 1. Strategy Pattern
**Purpose**: Select optimal elevator for requests

**Components**:
- `ElevatorSelectionStrategy` (interface)
- `NearestElevatorStrategy` (concrete strategy)
- `LeastLoadedStrategy` (concrete strategy)

**Benefits**:
- Easy to add new selection algorithms
- Runtime strategy switching
- Testable in isolation

### 2. Builder Pattern
**Purpose**: Flexible object construction

**Components**:
- `Building.Builder`
- `Request.Builder`

**Benefits**:
- Readable object creation
- Optional parameters
- Immutable objects

### 3. Observer Pattern
**Purpose**: Event notification system

**Components**:
- `ElevatorEventListener` (interface)
- `ElevatorDisplay` (concrete observer)

**Benefits**:
- Loose coupling
- Multiple observers
- Extensible notification system

### 4. Facade Pattern
**Purpose**: Simplified interface to complex subsystem

**Components**:
- `Building` class provides simple methods:
  - `requestElevatorUp(floor)`
  - `requestElevatorDown(floor)`
  - `selectFloorInElevator(elevatorId, floor)`

**Benefits**:
- Hide complexity
- Easy to use API
- Decoupled client code

## 🔄 System Flow

### 1. Hall Request Flow
```
User presses UP button on Floor 3
    ↓
HallPanel.pressUpButton()
    ↓
Building creates Request (HALL_REQUEST)
    ↓
Dispatcher receives request
    ↓
Strategy selects best elevator
    ↓
ElevatorController handles request
    ↓
Elevator moves to Floor 3
    ↓
Door opens → User enters → Selects destination
```

### 2. Internal Request Flow
```
User inside elevator presses Floor 7 button
    ↓
ElevatorPanel.pressButton(7)
    ↓
Building creates Request (ELEVATOR_REQUEST)
    ↓
ElevatorController adds destination
    ↓
Elevator moves to Floor 7
    ↓
Door opens → User exits
```

## 🧵 Concurrency Design

### Thread Model
- **Dispatcher Thread**: Processes request queue
- **Controller Threads**: One per elevator, manages movement
- **BlockingQueue**: Thread-safe request queue
- **Volatile flags**: Safe thread termination

### Thread Safety
- Synchronized access to shared state
- Immutable Request objects
- Thread-safe collections (TreeSet with proper synchronization)

## 📊 Class Diagram Structure

```
Building
├── Floor (multiple)
│   └── HallPanel
├── Elevator (multiple)
│   ├── Door
│   └── ElevatorPanel
├── ElevatorController (multiple)
└── ElevatorDispatcher
    └── ElevatorSelectionStrategy
```

## 🚀 How to Run

### Prerequisites
- Java 11 or higher
- Gradle

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew run
```

### Run from IDE
- Open the project in IntelliJ IDEA or Eclipse
- Run the `Main.java` class

## 🧪 Test Scenarios

The main application demonstrates:

1. **Single User Request**: User on floor 3 goes up to floor 7
2. **Multiple Concurrent Requests**: Users on different floors
3. **Multiple Destinations**: User selects multiple floors in sequence
4. **Peak Hour Simulation**: Many simultaneous requests

## 🔧 Extensibility

### Adding New Selection Strategy
```java
public class ZoneBasedStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        // Implement zone-based logic
    }
}

// Use it
Building building = new Building.Builder()
    .selectionStrategy(new ZoneBasedStrategy())
    .build();
```

### Adding New Event Listeners
```java
public class LoggingListener implements ElevatorEventListener {
    // Implement logging logic
}
```

### Adding New Features
- **Priority Requests**: Extend Request with priority field
- **Maintenance Mode**: Add maintenance state handling
- **Energy Optimization**: Strategy for energy-efficient routing
- **Access Control**: Add security levels to floors
- **Emergency Handling**: Fire mode, emergency stop

## 📈 Scalability Considerations

### Current Design Supports:
- ✅ Multiple elevators (configurable)
- ✅ Multiple floors (configurable)
- ✅ Concurrent requests
- ✅ Different selection algorithms
- ✅ Event notifications

### Future Enhancements:
- Distributed system support (multiple buildings)
- Persistent request queue (database)
- Real-time monitoring dashboard
- Machine learning for traffic prediction
- Load balancing across elevator banks

## 🎓 Learning Outcomes

This implementation demonstrates:
- Clean code principles
- SOLID design principles
- Design pattern application
- Concurrent programming
- Object-oriented design
- Modular architecture
- Testable code structure

## 📝 Code Quality

- **Modularity**: Each class has a single, well-defined purpose
- **Extensibility**: Easy to add new features without modifying existing code
- **Maintainability**: Clear structure and documentation
- **Testability**: Loosely coupled components
- **Readability**: Self-documenting code with meaningful names

## 🤝 Contributing

To extend this system:
1. Follow existing design patterns
2. Maintain SOLID principles
3. Add unit tests for new features
4. Document new components
5. Keep it simple and focused

## 📄 License

This is an educational project demonstrating low-level design principles.

---

**Author**: Elevator System Design Team  
**Purpose**: Low Level Design Demonstration  
**Date**: 2025
