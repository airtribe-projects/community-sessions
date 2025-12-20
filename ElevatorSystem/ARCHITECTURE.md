# Elevator System - Architecture Diagrams

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          BUILDING                               │
│  (Facade Pattern - Simplified Interface)                        │
│                                                                  │
│  + requestElevatorUp(floor)                                     │
│  + requestElevatorDown(floor)                                   │
│  + selectFloorInElevator(elevatorId, floor)                     │
│  + start()                                                       │
│  + stop()                                                        │
└────────────┬────────────────────────────────────────────────────┘
             │
             │ manages
             │
    ┌────────┴─────────┬──────────────────────┐
    │                  │                      │
    ▼                  ▼                      ▼
┌─────────┐    ┌──────────────┐    ┌──────────────────┐
│  FLOOR  │    │  ELEVATOR    │    │ DISPATCHER       │
│         │    │              │    │                  │
│ HAS-A   │    │ HAS-A Door   │    │ Uses Strategy    │
│ HallPanel│   │ HAS-A Panel  │    │                  │
└─────────┘    └──────┬───────┘    └────────┬─────────┘
                      │                     │
                      │ controlled by       │ dispatches to
                      ▼                     ▼
              ┌──────────────────┐  ┌──────────────────┐
              │ ELEVATOR         │  │ SELECTION        │
              │ CONTROLLER       │  │ STRATEGY         │
              │                  │  │ (Interface)      │
              │ Runnable Thread  │  └────────┬─────────┘
              └──────────────────┘           │
                                             │ implements
                                    ┌────────┴─────────┐
                                    │                  │
                            ┌───────▼──────┐  ┌───────▼──────┐
                            │ NEAREST      │  │ LEAST LOADED │
                            │ STRATEGY     │  │ STRATEGY     │
                            └──────────────┘  └──────────────┘
```

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         <<enumeration>>                          │
│                           Direction                              │
├─────────────────────────────────────────────────────────────────┤
│ + UP                                                             │
│ + DOWN                                                           │
│ + IDLE                                                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         <<enumeration>>                          │
│                         ElevatorState                            │
├─────────────────────────────────────────────────────────────────┤
│ + IDLE                                                           │
│ + MOVING_UP                                                      │
│ + MOVING_DOWN                                                    │
│ + DOOR_OPEN                                                      │
│ + MAINTENANCE                                                    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                            Request                               │
├─────────────────────────────────────────────────────────────────┤
│ - sourceFloor: int                                               │
│ - destinationFloor: Integer                                      │
│ - direction: Direction                                           │
│ - requestType: RequestType                                       │
│ - timestamp: LocalDateTime                                       │
├─────────────────────────────────────────────────────────────────┤
│ + getSourceFloor(): int                                          │
│ + getDestinationFloor(): Integer                                 │
│ + getDirection(): Direction                                      │
│ + getRequestType(): RequestType                                  │
│                                                                  │
│ <<inner class>> Builder                                          │
│   + sourceFloor(int): Builder                                    │
│   + destinationFloor(Integer): Builder                           │
│   + direction(Direction): Builder                                │
│   + requestType(RequestType): Builder                            │
│   + build(): Request                                             │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                             Door                                 │
├─────────────────────────────────────────────────────────────────┤
│ - state: DoorState                                               │
├─────────────────────────────────────────────────────────────────┤
│ + open(): void                                                   │
│ + close(): void                                                  │
│ + getState(): DoorState                                          │
│ + isOpen(): boolean                                              │
│ + isClosed(): boolean                                            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        ElevatorPanel                             │
├─────────────────────────────────────────────────────────────────┤
│ - elevatorId: int                                                │
│ - pressedButtons: Set<Integer>                                   │
├─────────────────────────────────────────────────────────────────┤
│ + pressButton(int): void                                         │
│ + clearButton(int): void                                         │
│ + getPressedButtons(): Set<Integer>                              │
│ + hasDestination(int): boolean                                   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         HallPanel                                │
├─────────────────────────────────────────────────────────────────┤
│ - floorNumber: int                                               │
│ - upButtonPressed: boolean                                       │
│ - downButtonPressed: boolean                                     │
├─────────────────────────────────────────────────────────────────┤
│ + pressUpButton(): void                                          │
│ + pressDownButton(): void                                        │
│ + clearUpButton(): void                                          │
│ + clearDownButton(): void                                        │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                            Floor                                 │
├─────────────────────────────────────────────────────────────────┤
│ - floorNumber: int                                               │
│ - hallPanel: HallPanel                    ◆────────┐             │
├─────────────────────────────────────────────────────────────────┤
│ + getFloorNumber(): int                                          │
│ + getHallPanel(): HallPanel                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                          Elevator                                │
├─────────────────────────────────────────────────────────────────┤
│ - id: int                                                        │
│ - currentFloor: int                                              │
│ - currentDirection: Direction                                    │
│ - state: ElevatorState                                           │
│ - door: Door                              ◆────────┐             │
│ - panel: ElevatorPanel                    ◆────────┐             │
│ - capacity: int                                                  │
│ - currentLoad: int                                               │
│ - upDestinations: TreeSet<Integer>                               │
│ - downDestinations: TreeSet<Integer>                             │
├─────────────────────────────────────────────────────────────────┤
│ + addDestination(int): void                                      │
│ + moveToFloor(int): void                                         │
│ + getNextDestination(): Integer                                  │
│ + hasDestinations(): boolean                                     │
│ + isAvailable(): boolean                                         │
│ + isIdle(): boolean                                              │
│ + getCurrentFloor(): int                                         │
│ + getCurrentDirection(): Direction                               │
│ + getState(): ElevatorState                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                  <<interface>>                                   │
│              ElevatorSelectionStrategy                           │
├─────────────────────────────────────────────────────────────────┤
│ + selectElevator(List<Elevator>, Request): Elevator              │
└─────────────────────────────────────────────────────────────────┘
                            △
                            │ implements
                ┌───────────┴───────────┐
                │                       │
┌───────────────▼────────┐  ┌───────────▼──────────┐
│ NearestElevatorStrategy│  │ LeastLoadedStrategy  │
├────────────────────────┤  ├──────────────────────┤
│ + selectElevator(...)  │  │ + selectElevator(...)│
└────────────────────────┘  └──────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    ElevatorController                            │
│                    implements Runnable                           │
├─────────────────────────────────────────────────────────────────┤
│ - elevator: Elevator                      ◆────────┐             │
│ - running: boolean (volatile)                                    │
├─────────────────────────────────────────────────────────────────┤
│ + run(): void                                                    │
│ + handleRequest(Request): void                                   │
│ + getElevator(): Elevator                                        │
│ + stop(): void                                                   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    ElevatorDispatcher                            │
│                    implements Runnable                           │
├─────────────────────────────────────────────────────────────────┤
│ - elevatorControllers: List<ElevatorController>                  │
│ - requestQueue: BlockingQueue<Request>                           │
│ - selectionStrategy: ElevatorSelectionStrategy  ◆────────┐       │
│ - running: boolean (volatile)                                    │
├─────────────────────────────────────────────────────────────────┤
│ + run(): void                                                    │
│ + submitRequest(Request): void                                   │
│ + stop(): void                                                   │
│ - dispatchRequest(Request): void                                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                          Building                                │
├─────────────────────────────────────────────────────────────────┤
│ - name: String                                                   │
│ - floors: List<Floor>                     ◆────────┐             │
│ - elevators: List<Elevator>               ◆────────┐             │
│ - elevatorControllers: List<ElevatorController>                  │
│ - dispatcher: ElevatorDispatcher          ◆────────┐             │
│ - controllerThreads: List<Thread>                                │
│ - dispatcherThread: Thread                                       │
├─────────────────────────────────────────────────────────────────┤
│ + start(): void                                                  │
│ + stop(): void                                                   │
│ + requestElevatorUp(int): void                                   │
│ + requestElevatorDown(int): void                                 │
│ + selectFloorInElevator(int, int): void                          │
│ + getName(): String                                              │
│ + getFloors(): List<Floor>                                       │
│ + getElevators(): List<Elevator>                                 │
│                                                                  │
│ <<inner class>> Builder                                          │
│   + name(String): Builder                                        │
│   + floors(int): Builder                                         │
│   + elevators(int, int): Builder                                 │
│   + selectionStrategy(ElevatorSelectionStrategy): Builder        │
│   + build(): Building                                            │
└─────────────────────────────────────────────────────────────────┘
```

## Sequence Diagram - Hall Request

```
User    HallPanel   Building   Dispatcher   Strategy   Controller   Elevator   Door
 │          │          │           │           │           │           │         │
 │ Press UP │          │           │           │           │           │         │
 │─────────>│          │           │           │           │           │         │
 │          │          │           │           │           │           │         │
 │          │ pressUpButton()      │           │           │           │         │
 │          │──────────│           │           │           │           │         │
 │          │          │           │           │           │           │         │
 │          │          │ submitRequest()       │           │           │         │
 │          │          │──────────>│           │           │           │         │
 │          │          │           │           │           │           │         │
 │          │          │           │ selectElevator()      │           │         │
 │          │          │           │──────────>│           │           │         │
 │          │          │           │           │           │           │         │
 │          │          │           │<──────────│           │           │         │
 │          │          │           │  (returns best elevator)          │         │
 │          │          │           │           │           │           │         │
 │          │          │           │ handleRequest()       │           │         │
 │          │          │           │──────────────────────>│           │         │
 │          │          │           │           │           │           │         │
 │          │          │           │           │           │ addDestination()    │
 │          │          │           │           │           │──────────>│         │
 │          │          │           │           │           │           │         │
 │          │          │           │           │           │           │ moveToFloor()
 │          │          │           │           │           │           │─────┐   │
 │          │          │           │           │           │           │     │   │
 │          │          │           │           │           │           │<────┘   │
 │          │          │           │           │           │           │         │
 │          │          │           │           │           │           │ open()  │
 │          │          │           │           │           │           │────────>│
 │          │          │           │           │           │           │         │
 │<─────────────────────────────────────────────────────────────────────────────│
 │                                  (Door opens)                                 │
 │                                                                               │
 │ Enter elevator                                                                │
 │──────────────────────────────────────────────────────────────────────────────>│
```

## Sequence Diagram - Internal Request

```
User    ElevatorPanel   Building   Controller   Elevator   Door
 │           │             │           │           │         │
 │ Press 7   │             │           │           │         │
 │──────────>│             │           │           │         │
 │           │             │           │           │         │
 │           │ pressButton(7)          │           │         │
 │           │─────────────│           │           │         │
 │           │             │           │           │         │
 │           │             │ selectFloorInElevator(1, 7)     │
 │           │             │──────────>│           │         │
 │           │             │           │           │         │
 │           │             │           │ addDestination(7)   │
 │           │             │           │──────────>│         │
 │           │             │           │           │         │
 │           │             │           │           │ moveToFloor(7)
 │           │             │           │           │─────┐   │
 │           │             │           │           │     │   │
 │           │             │           │           │<────┘   │
 │           │             │           │           │         │
 │           │             │           │           │ open()  │
 │           │             │           │           │────────>│
 │           │             │           │           │         │
 │<──────────────────────────────────────────────────────────│
 │                        (Door opens at floor 7)            │
```

## State Diagram - Elevator States

```
                    ┌──────────────┐
                    │              │
         ┌─────────>│     IDLE     │<─────────┐
         │          │              │          │
         │          └──────┬───────┘          │
         │                 │                  │
         │                 │ request received │
         │                 ▼                  │
         │          ┌──────────────┐          │
         │     ┌───>│  MOVING_UP   │──┐       │
         │     │    └──────────────┘  │       │
         │     │                      │       │
         │     │    ┌──────────────┐  │       │
         │     └────│ MOVING_DOWN  │<─┘       │
         │          └──────┬───────┘          │
         │                 │                  │
         │                 │ reached floor    │
         │                 ▼                  │
         │          ┌──────────────┐          │
         └──────────│  DOOR_OPEN   │──────────┘
                    └──────────────┘
                           │
                           │ maintenance needed
                           ▼
                    ┌──────────────┐
                    │ MAINTENANCE  │
                    └──────────────┘
```

## Component Interaction Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER ACTIONS                             │
└───────────────┬─────────────────────────────────────────────────┘
                │
                ├─── Press Hall Button (UP/DOWN)
                │    │
                │    └──> HallPanel.pressUpButton()
                │         │
                │         └──> Building.requestElevatorUp(floor)
                │              │
                │              └──> Create HALL_REQUEST
                │                   │
                │                   └──> Dispatcher.submitRequest()
                │
                └─── Press Elevator Button (Floor Number)
                     │
                     └──> ElevatorPanel.pressButton(floor)
                          │
                          └──> Building.selectFloorInElevator(id, floor)
                               │
                               └──> Create ELEVATOR_REQUEST
                                    │
                                    └──> Controller.handleRequest()

┌─────────────────────────────────────────────────────────────────┐
│                      REQUEST PROCESSING                          │
└───────────────┬─────────────────────────────────────────────────┘
                │
                ├─── Dispatcher Thread (continuously running)
                │    │
                │    ├──> Take request from BlockingQueue
                │    │
                │    ├──> Strategy.selectElevator(elevators, request)
                │    │    │
                │    │    ├──> Calculate best elevator
                │    │    │
                │    │    └──> Return selected elevator
                │    │
                │    └──> Controller.handleRequest(request)
                │
                └─── Controller Thread (per elevator, continuously running)
                     │
                     ├──> Check for next destination
                     │
                     ├──> Elevator.moveToFloor(destination)
                     │    │
                     │    ├──> Update state (MOVING_UP/MOVING_DOWN)
                     │    │
                     │    ├──> Move floor by floor
                     │    │
                     │    ├──> Check if should stop at each floor
                     │    │
                     │    └──> If stop: open door, wait, close door
                     │
                     └──> Repeat

┌─────────────────────────────────────────────────────────────────┐
│                      THREAD MANAGEMENT                           │
└───────────────┬─────────────────────────────────────────────────┘
                │
                ├─── Main Thread
                │    │
                │    ├──> Building.start()
                │    │    │
                │    │    ├──> Start Dispatcher Thread
                │    │    │
                │    │    └──> Start Controller Threads (one per elevator)
                │    │
                │    ├──> Run scenarios
                │    │
                │    └──> Building.stop()
                │         │
                │         ├──> Stop Dispatcher
                │         │
                │         └──> Stop all Controllers
                │
                ├─── Dispatcher Thread
                │    └──> Processes requests from queue
                │
                └─── Controller Threads (3 threads for 3 elevators)
                     └──> Each manages one elevator independently
```

## Design Pattern Visualization

### Strategy Pattern
```
┌─────────────────────────────────────────────────────────────────┐
│                        CONTEXT                                   │
│                   ElevatorDispatcher                             │
│                                                                  │
│  - strategy: ElevatorSelectionStrategy                           │
│                                                                  │
│  dispatchRequest(request) {                                      │
│      elevator = strategy.selectElevator(elevators, request)      │
│  }                                                               │
└────────────────────────┬────────────────────────────────────────┘
                         │ uses
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      <<interface>>                               │
│                ElevatorSelectionStrategy                         │
│                                                                  │
│  + selectElevator(elevators, request): Elevator                  │
└────────────────────────┬────────────────────────────────────────┘
                         │ implements
          ┌──────────────┴──────────────┐
          ▼                             ▼
┌──────────────────────┐      ┌──────────────────────┐
│ NearestElevator      │      │ LeastLoaded          │
│ Strategy             │      │ Strategy             │
│                      │      │                      │
│ selectElevator() {   │      │ selectElevator() {   │
│   // nearest logic   │      │   // load balance    │
│ }                    │      │ }                    │
└──────────────────────┘      └──────────────────────┘
```

### Builder Pattern
```
┌─────────────────────────────────────────────────────────────────┐
│                         Building                                 │
│                                                                  │
│  private Building(Builder builder) {                             │
│      this.name = builder.name;                                   │
│      this.floors = builder.floors;                               │
│      this.elevators = builder.elevators;                         │
│  }                                                               │
│                                                                  │
│  static class Builder {                                          │
│      private String name;                                        │
│      private List<Floor> floors;                                 │
│      private List<Elevator> elevators;                           │
│                                                                  │
│      Builder name(String name) { ... return this; }              │
│      Builder floors(int n) { ... return this; }                  │
│      Builder elevators(int n, int cap) { ... return this; }      │
│      Building build() { return new Building(this); }             │
│  }                                                               │
└─────────────────────────────────────────────────────────────────┘

Usage:
Building building = new Building.Builder()
    .name("Tech Tower")
    .floors(10)
    .elevators(3, 10)
    .build();
```

### Observer Pattern
```
┌─────────────────────────────────────────────────────────────────┐
│                      <<interface>>                               │
│                  ElevatorEventListener                           │
│                                                                  │
│  + onElevatorMoved(elevator, from, to)                           │
│  + onElevatorDoorOpened(elevator)                                │
│  + onElevatorDoorClosed(elevator)                                │
│  + onElevatorStateChanged(elevator)                              │
└────────────────────────┬────────────────────────────────────────┘
                         │ implements
          ┌──────────────┴──────────────┐
          ▼                             ▼
┌──────────────────────┐      ┌──────────────────────┐
│ ElevatorDisplay      │      │ LoggingListener      │
│                      │      │                      │
│ onElevatorMoved() {  │      │ onElevatorMoved() {  │
│   // display update  │      │   // log event       │
│ }                    │      │ }                    │
└──────────────────────┘      └──────────────────────┘
```

## Concurrency Model

```
┌─────────────────────────────────────────────────────────────────┐
│                        MAIN THREAD                               │
│                                                                  │
│  - Creates Building                                              │
│  - Starts system (spawns threads)                                │
│  - Submits user requests                                         │
│  - Stops system                                                  │
└────────────────────────┬────────────────────────────────────────┘
                         │ spawns
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ DISPATCHER   │  │ CONTROLLER-1 │  │ CONTROLLER-2 │  ...
│ THREAD       │  │ THREAD       │  │ THREAD       │
│              │  │              │  │              │
│ while(run) { │  │ while(run) { │  │ while(run) { │
│   request =  │  │   process    │  │   process    │
│   queue.take │  │   elevator-1 │  │   elevator-2 │
│   dispatch() │  │ }            │  │ }            │
│ }            │  │              │  │              │
└──────────────┘  └──────────────┘  └──────────────┘
       │                 │                 │
       │                 │                 │
       └─────────────────┴─────────────────┘
                         │
                         ▼
              ┌─────────────────────┐
              │  BlockingQueue      │
              │  <Request>          │
              │                     │
              │  Thread-safe queue  │
              │  for requests       │
              └─────────────────────┘
```

---

This architecture ensures:
- ✅ **Modularity**: Clear separation of concerns
- ✅ **Scalability**: Easy to add more elevators/floors
- ✅ **Extensibility**: New strategies and features
- ✅ **Thread Safety**: Proper concurrency handling
- ✅ **Maintainability**: Clean, documented design
