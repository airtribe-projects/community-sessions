# Elevator System - Quick Start Guide

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Gradle (included via wrapper)

### Build the Project
```bash
cd ElevatorSystem
./gradlew build
```

### Run the Application
```bash
./gradlew run
```

Or run from IDE:
- Open `Main.java`
- Click Run

---

## 📖 Quick API Reference

### Creating a Building

```java
Building building = new Building.Builder()
    .name("Tech Tower")
    .floors(10)                    // 10 floors (0-9)
    .elevators(3, 10)              // 3 elevators, capacity 10
    .selectionStrategy(new NearestElevatorStrategy())
    .build();
```

### Starting the System

```java
building.start();  // Starts all elevator controllers and dispatcher
```

### User Actions

#### 1. Request Elevator from Floor (Hall Button)
```java
// User on floor 3 wants to go UP
building.requestElevatorUp(3);

// User on floor 8 wants to go DOWN
building.requestElevatorDown(8);
```

#### 2. Select Destination Inside Elevator
```java
// User inside elevator 1 selects floor 7
building.selectFloorInElevator(1, 7);
```

### Stopping the System

```java
building.stop();  // Gracefully stops all threads
```

---

## 🎯 Usage Examples

### Example 1: Single User Journey
```java
// User on floor 3 wants to go to floor 7
building.requestElevatorUp(3);           // Press UP button
Thread.sleep(5000);                      // Wait for elevator
building.selectFloorInElevator(1, 7);    // Select floor 7 inside
```

### Example 2: Multiple Concurrent Requests
```java
// Multiple users on different floors
building.requestElevatorUp(2);
building.requestElevatorDown(8);
building.requestElevatorUp(5);
```

### Example 3: Multiple Destinations
```java
// User selects multiple floors
building.selectFloorInElevator(1, 4);
building.selectFloorInElevator(1, 6);
building.selectFloorInElevator(1, 9);
// Elevator will stop at 4, 6, and 9 in order
```

### Example 4: Custom Strategy
```java
// Use different selection strategy
Building building = new Building.Builder()
    .name("Office Building")
    .floors(20)
    .elevators(5, 15)
    .selectionStrategy(new LeastLoadedStrategy())  // Load balancing
    .build();
```

---

## 🏗️ Project Structure

```
ElevatorSystem/
├── src/main/java/org/example/
│   ├── Main.java                          # Entry point
│   │
│   ├── enums/                             # Enumerations
│   │   ├── Direction.java
│   │   ├── ElevatorState.java
│   │   ├── DoorState.java
│   │   └── RequestType.java
│   │
│   ├── models/                            # Core domain models
│   │   ├── Building.java                  # Main facade
│   │   ├── Floor.java
│   │   ├── Elevator.java
│   │   ├── Door.java
│   │   ├── ElevatorPanel.java
│   │   ├── HallPanel.java
│   │   └── Request.java
│   │
│   ├── controller/                        # Controllers
│   │   ├── ElevatorController.java
│   │   └── ElevatorDispatcher.java
│   │
│   ├── strategy/                          # Strategy pattern
│   │   ├── ElevatorSelectionStrategy.java
│   │   ├── NearestElevatorStrategy.java
│   │   └── LeastLoadedStrategy.java
│   │
│   └── observer/                          # Observer pattern
│       ├── ElevatorEventListener.java
│       └── ElevatorDisplay.java
│
├── README.md                              # Overview
├── DESIGN.md                              # Detailed design
├── ARCHITECTURE.md                        # Architecture diagrams
├── QUICK_START.md                         # This file
└── build.gradle                           # Build configuration
```

---

## 🎨 Key Design Patterns

### 1. Strategy Pattern
**Where**: Elevator selection algorithms  
**Why**: Easy to add new selection strategies

```java
// Switch strategies at runtime
new NearestElevatorStrategy()
new LeastLoadedStrategy()
```

### 2. Builder Pattern
**Where**: Building and Request construction  
**Why**: Flexible, readable object creation

```java
Building building = new Building.Builder()
    .name("Tower")
    .floors(10)
    .build();
```

### 3. Facade Pattern
**Where**: Building class  
**Why**: Simple interface to complex system

```java
building.requestElevatorUp(3);  // Simple API
```

### 4. Observer Pattern
**Where**: Event notifications  
**Why**: Loose coupling for event handling

```java
ElevatorEventListener listener = new ElevatorDisplay("Main Display");
```

---

## 🔧 Customization

### Add New Selection Strategy

1. Create new class implementing `ElevatorSelectionStrategy`
```java
public class MyStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        // Your logic here
    }
}
```

2. Use it when building
```java
Building building = new Building.Builder()
    .selectionStrategy(new MyStrategy())
    .build();
```

### Add Event Listener

1. Implement `ElevatorEventListener`
```java
public class MyListener implements ElevatorEventListener {
    @Override
    public void onElevatorMoved(Elevator elevator, int from, int to) {
        // Your logic
    }
    // ... implement other methods
}
```

---

## 📊 System Behavior

### Elevator Selection (Nearest Strategy)
1. Calculates distance to each elevator
2. Prefers elevators moving in same direction
3. Prefers idle elevators
4. Selects closest match

### Elevator Movement
1. Adds destination to appropriate queue (up/down)
2. Moves floor by floor
3. Stops at intermediate floors if needed
4. Opens door, waits, closes door
5. Continues to next destination

### Request Processing
1. User action creates Request
2. Request added to BlockingQueue
3. Dispatcher selects best elevator
4. Controller handles request
5. Elevator moves to destination

---

## 🧪 Testing Scenarios

The demo includes:

1. **Single User**: Floor 3 → Floor 7
2. **Multiple Users**: Concurrent requests from different floors
3. **Multiple Destinations**: User selects 3 floors in sequence
4. **Peak Hour**: 6 simultaneous requests

---

## 🐛 Troubleshooting

### Build Fails
```bash
# Clean and rebuild
./gradlew clean build
```

### Threads Don't Stop
```bash
# Ensure you call building.stop()
# Check for InterruptedException handling
```

### Elevator Doesn't Move
- Check if elevator is in MAINTENANCE state
- Verify request is properly created
- Check console output for errors

---

## 📈 Performance Tips

1. **Adjust Thread Sleep Times**: Reduce for faster simulation
2. **Increase Elevators**: Better handling of concurrent requests
3. **Choose Right Strategy**: 
   - `NearestElevatorStrategy` for response time
   - `LeastLoadedStrategy` for load balancing

---

## 🎓 Learning Path

1. **Start with**: `Main.java` - See how it all works
2. **Then explore**: `Building.java` - Understand the facade
3. **Deep dive**: `Elevator.java` - Core logic
4. **Study patterns**: Strategy, Builder, Observer
5. **Extend**: Add your own features

---

## 📚 Documentation

- **README.md**: Project overview and features
- **DESIGN.md**: Detailed design principles and patterns
- **ARCHITECTURE.md**: Visual diagrams and architecture
- **QUICK_START.md**: This guide

---

## 💡 Tips

- **Start Simple**: Run the demo first
- **Read Console Output**: Shows all elevator movements
- **Experiment**: Change parameters and see effects
- **Extend**: Add new features following existing patterns

---

## 🤝 Need Help?

1. Check console output for detailed logs
2. Review DESIGN.md for architecture details
3. Look at ARCHITECTURE.md for visual diagrams
4. Study existing code patterns

---

**Happy Coding! 🚀**
