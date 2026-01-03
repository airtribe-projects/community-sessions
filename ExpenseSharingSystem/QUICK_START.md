# Quick Start Guide - Expense Sharing System

## 🚀 Run the Application

```bash
./gradlew run
```

## 📦 What's Included

### Complete Implementation
✅ **11 Classes** - All entities, strategies, and services  
✅ **3 Split Strategies** - Equal, Exact, Percentage  
✅ **4 Services** - User, Expense, Group, Balance management  
✅ **Working Demo** - 9 comprehensive scenarios  
✅ **Full Documentation** - Design docs, diagrams, thought process  

## 📂 File Structure

```
ExpenseSharingSystem/
│
├── src/main/java/org/example/
│   ├── model/                          # 6 entity classes
│   │   ├── User.java
│   │   ├── Expense.java
│   │   ├── Split.java
│   │   ├── Group.java
│   │   ├── Balance.java
│   │   └── SplitType.java
│   │
│   ├── strategy/                       # Strategy Pattern
│   │   ├── ExpenseSplitStrategy.java
│   │   ├── EqualSplitStrategy.java
│   │   ├── ExactSplitStrategy.java
│   │   └── PercentageSplitStrategy.java
│   │
│   ├── service/                        # Service Layer
│   │   ├── UserService.java
│   │   ├── ExpenseService.java
│   │   ├── GroupService.java
│   │   └── BalanceManager.java
│   │
│   └── Main.java                       # Demo application
│
├── DESIGN.md                           # Complete design document
├── CLASS_DIAGRAM.md                    # Visual class diagram
├── THOUGHT_PROCESS.md                  # Design thinking process
├── ENTITIES_SUMMARY.md                 # Entity reference guide
├── class-diagram.puml                  # PlantUML diagram
├── README.md                           # Full documentation
└── QUICK_START.md                      # This file
```

## 🎯 Code Statistics

- **Total Lines**: ~2000+ lines of production code
- **Classes**: 15 (6 entities + 4 strategies + 4 services + 1 main)
- **Design Patterns**: 3 (Strategy, Service Layer, Composition)
- **SOLID Principles**: All 5 applied throughout

## 💻 Code Highlights

### 1. Strategy Pattern in Action
```java
// ExpenseService automatically selects the right strategy
public Expense createExpense(..., SplitType splitType, ...) {
    ExpenseSplitStrategy strategy = strategyMap.get(splitType);
    List<Split> splits = strategy.calculateSplits(...);
    // ...
}
```

### 2. Clean Service Layer
```java
// Services handle all business logic
UserService userService = new UserService();
ExpenseService expenseService = new ExpenseService(balanceManager);
GroupService groupService = new GroupService(expenseService);
```

### 3. Immutable Entities
```java
// Expense is immutable after creation
public class Expense {
    private final String id;
    private final Double totalAmount;
    // ... all fields are final
}
```

### 4. Defensive Copying
```java
public List<Split> getSplits() {
    return new ArrayList<>(splits); // Return copy, not original
}
```

## 🎓 Learning Path

### For Students
1. **Read**: `THOUGHT_PROCESS.md` - Understand the design thinking
2. **Study**: `CLASS_DIAGRAM.md` - Visualize the architecture
3. **Review**: Source code - See principles in action
4. **Run**: `./gradlew run` - See it working
5. **Extend**: Add a new split strategy

### For Instructors
1. **Present**: `DESIGN.md` - Comprehensive design overview
2. **Discuss**: SOLID principles with code examples
3. **Demonstrate**: Live coding a new feature
4. **Exercise**: Students implement new split type
5. **Review**: Code review session

## 🔧 Common Tasks

### Add a New Split Strategy

1. **Create Strategy Class**:
```java
package org.example.strategy;

public class ShareBasedSplitStrategy implements ExpenseSplitStrategy {
    @Override
    public List<Split> calculateSplits(Expense expense, 
                                       List<User> participants, 
                                       Map<String, Object> metadata) {
        // Your logic here
    }
    
    @Override
    public boolean validate(Expense expense, 
                           List<User> participants, 
                           Map<String, Object> metadata) {
        // Your validation here
    }
}
```

2. **Add to SplitType Enum**:
```java
public enum SplitType {
    EQUAL,
    EXACT,
    PERCENTAGE,
    SHARE_BASED  // Add this
}
```

3. **Register in ExpenseService**:
```java
registerStrategy(SplitType.SHARE_BASED, new ShareBasedSplitStrategy());
```

### Run Specific Demo

Modify `Main.java` to comment out demos you don't want to run:

```java
public static void main(String[] args) {
    // ... initialization ...
    
    demo1_CreateUsers(userService);
    demo2_CreateGroup(groupService, userService);
    demo3_EqualSplit(expenseService, userService, tripGroup);
    // demo4_ExactSplit(...);  // Comment out to skip
    // demo5_PercentageSplit(...);  // Comment out to skip
}
```

## 📊 Demo Output Explained

### Equal Split Demo
```
✓ Expense created: Dinner at Beach Restaurant (3000.00)
  Splits:
    - Alice Johnson: 1000.00
    - Bob Smith: 1000.00
    - Charlie Brown: 1000.00
```
**Explanation**: 3000 ÷ 3 = 1000 each

### Balance Tracking
```
=== Current Balances ===
  User U0001 owes User U0002: 1500.00
  User U0002 owes User U0001: 1000.00
```
**Explanation**: Net balance is calculated across all expenses

### Payment Settlement
```
✓ Payment recorded: Bob Smith paid 500.00 to Alice Johnson
```
**Explanation**: Reduces Bob's debt to Alice by 500

## 🎨 Design Principles Demonstrated

### Single Responsibility Principle
- `User` only manages user data
- `ExpenseService` only handles expenses
- `BalanceManager` only tracks balances

### Open/Closed Principle
- Add new split type without modifying existing code
- Just implement `ExpenseSplitStrategy` interface

### Liskov Substitution Principle
- All strategies are interchangeable
- Any `ExpenseSplitStrategy` works the same way

### Interface Segregation Principle
- `ExpenseSplitStrategy` has only 2 methods
- No unnecessary methods

### Dependency Inversion Principle
- `ExpenseService` depends on `ExpenseSplitStrategy` interface
- Not on concrete implementations

## 🧪 Testing Ideas

### Unit Tests to Write
```java
@Test
public void testEqualSplit() {
    // Test equal split calculation
}

@Test
public void testExactSplitValidation() {
    // Test validation logic
}

@Test
public void testBalanceUpdate() {
    // Test balance tracking
}
```

### Integration Tests to Write
```java
@Test
public void testCompleteExpenseFlow() {
    // Create users, group, expense, check balances
}
```

## 🚀 Next Steps

### Beginner
1. Run the application
2. Understand the output
3. Read the entity classes
4. Trace one expense creation flow

### Intermediate
1. Implement a new split strategy
2. Add unit tests
3. Add expense categories
4. Implement currency support

### Advanced
1. Implement debt simplification algorithm
2. Add persistence layer (database)
3. Create REST API
4. Build web UI

## 📚 Documentation Files

| File | Purpose | Audience |
|------|---------|----------|
| `README.md` | Complete documentation | Everyone |
| `DESIGN.md` | Design principles & patterns | Students/Instructors |
| `CLASS_DIAGRAM.md` | Visual architecture | Visual learners |
| `THOUGHT_PROCESS.md` | Design thinking | Advanced students |
| `ENTITIES_SUMMARY.md` | Quick reference | Developers |
| `QUICK_START.md` | Get started quickly | Beginners |

## 💡 Key Takeaways

1. ✅ **Strategy Pattern** makes adding new features easy
2. ✅ **Service Layer** separates concerns cleanly
3. ✅ **Immutability** ensures data consistency
4. ✅ **SOLID Principles** lead to maintainable code
5. ✅ **Composition** is more flexible than inheritance

## 🤝 Support

For questions or issues:
1. Read the documentation files
2. Review the code comments
3. Run the demo and observe output
4. Experiment with modifications

---

**Happy Learning! 🎓**

*This is a complete, production-ready implementation suitable for teaching Low Level Design concepts.*
