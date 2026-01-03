# Expense Sharing System (Splitwise Clone)

A complete implementation of an expense sharing system similar to Splitwise, demonstrating SOLID principles, design patterns, and best practices in object-oriented design.

## 🎯 Purpose

This project is designed for teaching Low Level Design (LLD) concepts, showcasing:
- **SOLID Principles** in action
- **Design Patterns** (Strategy, Service Layer, Composition)
- **Clean Code** practices
- **Modular & Extensible** architecture

## 📁 Project Structure

```
src/main/java/org/example/
├── model/                  # Entity Layer
│   ├── User.java          # User entity
│   ├── Expense.java       # Expense entity
│   ├── Split.java         # Split entity
│   ├── Group.java         # Group entity
│   ├── Balance.java       # Balance entity
│   └── SplitType.java     # Enum for split types
│
├── strategy/              # Strategy Pattern Layer
│   ├── ExpenseSplitStrategy.java        # Strategy interface
│   ├── EqualSplitStrategy.java          # Equal split implementation
│   ├── ExactSplitStrategy.java          # Exact split implementation
│   └── PercentageSplitStrategy.java     # Percentage split implementation
│
├── service/               # Service Layer
│   ├── UserService.java          # User management
│   ├── ExpenseService.java       # Expense operations
│   ├── GroupService.java         # Group management
│   └── BalanceManager.java       # Balance tracking
│
└── Main.java              # Demo application
```

## 🚀 How to Run

### Prerequisites
- Java 8 or higher
- Gradle (included via wrapper)

### Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run
```

Or using Java directly:
```bash
# Compile
javac -d bin src/main/java/org/example/**/*.java

# Run
java -cp bin org.example.Main
```

## 💡 Features

### Core Functionality
- ✅ **User Management**: Create and manage users
- ✅ **Group Management**: Create groups of users
- ✅ **Expense Creation**: Create expenses with different split types
- ✅ **Split Strategies**:
  - Equal Split (divide equally)
  - Exact Split (specify exact amounts)
  - Percentage Split (specify percentages)
- ✅ **Balance Tracking**: Track who owes whom
- ✅ **Payment Settlement**: Record payments between users
- ✅ **Expense History**: View expenses by user or group

### Design Features
- ✅ **Strategy Pattern**: Easy to add new split types
- ✅ **Service Layer**: Clean separation of concerns
- ✅ **Immutable Entities**: Thread-safe expense records
- ✅ **Defensive Copying**: Protect internal state
- ✅ **Validation**: Input validation at multiple levels

## 🏗️ Architecture & Design

### SOLID Principles

#### Single Responsibility Principle (SRP)
- Each class has one clear responsibility
- `User` only manages user data
- `ExpenseService` only handles expense operations
- `BalanceManager` only handles balance calculations

#### Open/Closed Principle (OCP)
- System is open for extension (new split strategies)
- Closed for modification (existing code unchanged)
- Add new split type by implementing `ExpenseSplitStrategy`

#### Liskov Substitution Principle (LSP)
- All strategy implementations are interchangeable
- Any `ExpenseSplitStrategy` can be used without breaking code

#### Interface Segregation Principle (ISP)
- Small, focused interfaces
- `ExpenseSplitStrategy` has only necessary methods

#### Dependency Inversion Principle (DIP)
- High-level modules depend on abstractions
- `ExpenseService` depends on `ExpenseSplitStrategy` interface

### Design Patterns

#### 1. Strategy Pattern
**Location**: `strategy` package

**Purpose**: Different algorithms for splitting expenses

**Benefits**:
- Easy to add new split types
- Each strategy is independently testable
- Follows OCP

**Example**:
```java
// Adding a new split strategy
public class ShareBasedSplitStrategy implements ExpenseSplitStrategy {
    @Override
    public List<Split> calculateSplits(...) {
        // Custom logic for share-based splitting
    }
}
```

#### 2. Service Layer Pattern
**Location**: `service` package

**Purpose**: Separate business logic from entities

**Benefits**:
- Entities remain simple POJOs
- Better testability
- Clear separation of concerns

#### 3. Composition Over Inheritance
**Examples**:
- `Group` contains `User` objects (HAS-A, not IS-A)
- `Expense` uses `ExpenseSplitStrategy` (composition, not inheritance)

### Key Design Decisions

1. **Immutable Expenses**: Once created, expenses cannot be modified (ensures data consistency)
2. **Defensive Copying**: Collections are copied to prevent external modification
3. **Strategy Registry**: `ExpenseService` maintains a map of strategies for runtime selection
4. **Balance Tracking**: Separate `BalanceManager` for complex balance calculations

## 📚 Usage Examples

### Example 1: Equal Split
```java
// Create users
User alice = userService.createUser("Alice", "alice@example.com", "1234567890");
User bob = userService.createUser("Bob", "bob@example.com", "0987654321");
User charlie = userService.createUser("Charlie", "charlie@example.com", "1122334455");

// Create expense with equal split
Expense expense = expenseService.createExpense(
    "Dinner",
    3000.0,
    alice,
    Arrays.asList(alice, bob, charlie),
    SplitType.EQUAL,
    null
);
// Result: Each person owes 1000
```

### Example 2: Exact Split
```java
Map<User, Double> exactAmounts = new HashMap<>();
exactAmounts.put(alice, 1500.0);
exactAmounts.put(bob, 2000.0);
exactAmounts.put(charlie, 1500.0);

Expense expense = expenseService.createExpenseWithExactAmounts(
    "Hotel",
    5000.0,
    bob,
    exactAmounts,
    null
);
```

### Example 3: Percentage Split
```java
Map<User, Double> percentages = new HashMap<>();
percentages.put(alice, 30.0);   // 30%
percentages.put(bob, 40.0);     // 40%
percentages.put(charlie, 30.0); // 30%

Expense expense = expenseService.createExpenseWithPercentages(
    "Activities",
    4000.0,
    charlie,
    percentages,
    null
);
```

### Example 4: View Balances
```java
// Print all balances
balanceManager.printAllBalances();

// Get balance between two users
Double balance = balanceManager.getBalance(alice, bob);
```

### Example 5: Settle Payment
```java
// Bob pays Alice 500
balanceManager.recordPayment(bob, alice, 500.0);
```

## 🧪 Testing Strategy

### Unit Tests (Recommended)
- Test each split strategy independently
- Test balance calculations
- Test validation logic
- Mock dependencies for service tests

### Integration Tests (Recommended)
- Test complete expense creation flow
- Test balance updates across multiple expenses
- Test group operations

### Edge Cases to Test
- Zero amount expenses
- Single user expenses
- Rounding errors in splits
- Negative balances
- Invalid percentages (not summing to 100)
- Invalid exact amounts (not summing to total)

## 🔧 Extensibility

### Adding New Split Type

1. Create new strategy class:
```java
public class CustomSplitStrategy implements ExpenseSplitStrategy {
    @Override
    public List<Split> calculateSplits(...) {
        // Your custom logic
    }
    
    @Override
    public boolean validate(...) {
        // Your validation logic
    }
}
```

2. Register in `ExpenseService`:
```java
registerStrategy(SplitType.CUSTOM, new CustomSplitStrategy());
```

### Adding Notification System

1. Create interface:
```java
public interface NotificationService {
    void notifyExpenseCreated(Expense expense);
    void notifyPaymentReceived(User user, Double amount);
}
```

2. Implement:
```java
public class EmailNotificationService implements NotificationService {
    // Implementation
}
```

3. Inject into services

### Adding Persistence Layer

1. Create repository interfaces:
```java
public interface UserRepository {
    User save(User user);
    User findById(String id);
    List<User> findAll();
}
```

2. Implement (in-memory, database, etc.)
3. Inject into services

## 📖 Documentation

- **DESIGN.md**: Complete design document with principles and patterns
- **CLASS_DIAGRAM.md**: Visual class diagram with relationships
- **THOUGHT_PROCESS.md**: Step-by-step design thinking process
- **ENTITIES_SUMMARY.md**: Quick reference for all entities
- **class-diagram.puml**: PlantUML diagram (can be rendered)

## 🎓 Learning Objectives

After studying this codebase, you should understand:

1. ✅ How to apply SOLID principles in real code
2. ✅ When and how to use Strategy Pattern
3. ✅ How to design extensible systems
4. ✅ Service layer architecture
5. ✅ Composition over inheritance
6. ✅ Immutability and defensive copying
7. ✅ Clean code practices

## 🤝 Contributing

This is an educational project. Feel free to:
- Add new features (recurring expenses, categories, etc.)
- Implement additional split strategies
- Add unit tests
- Improve documentation
- Optimize algorithms

## 📝 License

This project is for educational purposes.

## 👨‍🏫 For Instructors

### Teaching Points

1. **Start with Requirements**: Discuss what Splitwise does
2. **Identify Entities**: Walk through entity identification process
3. **Apply SOLID**: Show how each principle is applied
4. **Introduce Patterns**: Explain why Strategy Pattern was chosen
5. **Discuss Trade-offs**: Talk about design decisions and alternatives
6. **Live Coding**: Implement a new split strategy together
7. **Code Review**: Have students review and critique the design

### Discussion Questions

1. Why use Strategy Pattern instead of if-else statements?
2. What are the benefits of immutable expenses?
3. How would you add currency support?
4. How would you implement debt simplification?
5. What are the trade-offs of eager vs lazy balance calculation?
6. How would you scale this to millions of users?

## 🔗 Related Concepts

- **Design Patterns**: Strategy, Factory, Observer, Repository
- **Principles**: SOLID, DRY, KISS, YAGNI
- **Architecture**: Layered Architecture, Clean Architecture
- **Data Structures**: Graphs (for debt simplification)
- **Algorithms**: Greedy algorithms (for minimizing transactions)

---

**Built with ❤️ for teaching Low Level Design**
