# Expense Sharing System - Low Level Design

## System Overview
An expense sharing system similar to Splitwise that allows users to split expenses among groups, track balances, and settle debts.

---

## Core Requirements Analysis

### Functional Requirements
1. **User Management**: Add/manage users
2. **Expense Creation**: Create expenses with different split types
3. **Split Strategies**: Equal, Exact, Percentage splits
4. **Balance Tracking**: Track who owes whom
5. **Settlement**: Record payments between users
6. **Group Management**: Create and manage groups
7. **Expense History**: View past expenses

### Non-Functional Requirements
- **Extensible**: Easy to add new split types
- **Scalable**: Handle multiple users and groups
- **Modular**: Clear separation of concerns
- **Maintainable**: Follow SOLID principles

---

## Design Principles Applied

### 1. **SOLID Principles**

#### Single Responsibility Principle (SRP)
- Each class has one reason to change
- `User` only manages user data
- `Expense` only manages expense data
- `ExpenseSplitStrategy` only handles split logic
- `BalanceManager` only handles balance calculations

#### Open/Closed Principle (OCP)
- System is open for extension (new split strategies) but closed for modification
- Strategy pattern for split types allows adding new strategies without modifying existing code

#### Liskov Substitution Principle (LSP)
- All `ExpenseSplitStrategy` implementations can be used interchangeably
- Subtypes maintain the contract of their base type

#### Interface Segregation Principle (ISP)
- Small, focused interfaces
- `ExpenseSplitStrategy` interface has single method
- Clients depend only on interfaces they use

#### Dependency Inversion Principle (DIP)
- High-level modules depend on abstractions (interfaces)
- `ExpenseService` depends on `ExpenseSplitStrategy` interface, not concrete implementations
- Enables dependency injection

### 2. **DRY (Don't Repeat Yourself)**
- Split logic is not duplicated across expense types
- Balance calculation logic centralized in `BalanceManager`
- Common validation logic in base classes

### 3. **KISS (Keep It Simple, Stupid)**
- Clear, straightforward class structure
- No over-engineering
- Simple method signatures

### 4. **YAGNI (You Aren't Gonna Need It)**
- Only implementing features needed now
- No speculative generality
- Can extend later when needed

### 5. **Composition Over Inheritance**
- `Expense` uses composition with `ExpenseSplitStrategy` (Strategy pattern)
- `Group` contains `User` objects rather than extending
- Flexible and avoids deep inheritance hierarchies

---

## Core Entities Identified

### 1. **User**
- Represents a person in the system
- Attributes: id, name, email, phone
- Responsibilities: Store user information

### 2. **Expense**
- Represents a single expense transaction
- Attributes: id, description, amount, paidBy, participants, splitType, timestamp
- Responsibilities: Store expense details and split information

### 3. **Split**
- Represents how much each user owes in an expense
- Attributes: user, amount, percentage (optional)
- Responsibilities: Store individual share information

### 4. **Group**
- Represents a collection of users
- Attributes: id, name, members, expenses
- Responsibilities: Manage group members and group expenses

### 5. **Balance**
- Represents amount owed between two users
- Attributes: fromUser, toUser, amount
- Responsibilities: Track debt between users

### 6. **ExpenseSplitStrategy** (Interface)
- Strategy for calculating splits
- Implementations: EqualSplitStrategy, ExactSplitStrategy, PercentageSplitStrategy
- Responsibilities: Calculate how expense is split among participants

### 7. **ExpenseService**
- Core service for expense operations

### 8. **BalanceManager**
- Manages balance calculations and simplifications
- Responsibilities: Calculate balances, simplify debts, get user balances

### 9. **GroupService**
- Manages group operations
- Responsibilities: Create groups, add/remove members, query group data
- **Note**: Does NOT handle adding expenses to groups - that's `ExpenseService`'s responsibility (SRP)

### 10. **UserService**
- Manages user operations
- Responsibilities: Create users, retrieve user information

{{ ... }}
---

## Design Patterns Used

### 1. **Strategy Pattern**
- **Where**: `ExpenseSplitStrategy` and its implementations
- **Why**: Allows runtime selection of split algorithm
- **Benefit**: Easy to add new split types without modifying existing code (OCP)

### 2. **Factory Pattern** (Optional Enhancement)
- **Where**: `SplitStrategyFactory` to create appropriate strategy
- **Why**: Centralize strategy creation logic
- **Benefit**: Cleaner client code, easier to manage strategy creation

### 3. **Service Layer Pattern**
- **Where**: `ExpenseService`, `GroupService`, `UserService`
- **Why**: Separate business logic from data models
- **Benefit**: Better testability, clear separation of concerns

### 4. **Repository Pattern** (For future persistence)
- **Where**: Can add `UserRepository`, `ExpenseRepository`, `GroupRepository`
- **Why**: Abstract data access layer
- **Benefit**: Easy to swap storage mechanisms

---

## Class Relationships

### Associations
- `User` ↔ `Group` (many-to-many)
- `User` ↔ `Expense` (one-to-many as paidBy)
- `Expense` ↔ `Split` (one-to-many)
- `Split` → `User` (many-to-one)
- `Group` → `Expense` (one-to-many)
- `Balance` → `User` (two many-to-one relationships)

### Dependencies
- `ExpenseService` → `ExpenseSplitStrategy` (depends on interface)
- `ExpenseService` → `BalanceManager`
- `GroupService` → `ExpenseService`
- All concrete strategies implement `ExpenseSplitStrategy`

### Composition
- `Expense` contains `List<Split>`
- `Group` contains `List<User>` and `List<Expense>`
- `Split` contains `User` reference

---

## Key Design Decisions

### 1. **Why Strategy Pattern for Split Types?**
- **Problem**: Different ways to split expenses (equal, exact, percentage)
- **Solution**: Strategy pattern allows selecting algorithm at runtime
- **Alternative Rejected**: Using if-else or switch statements would violate OCP
- **Benefit**: Adding new split type requires only creating new strategy class

### 2. **Why Separate Balance from Expense?**
- **Problem**: Need to track net balances across multiple expenses
- **Solution**: Separate `Balance` entity and `BalanceManager` service
- **Benefit**: Can calculate and simplify balances independently of expenses

### 3. **Why Service Layer?**
- **Problem**: Business logic shouldn't be in entity classes
- **Solution**: Separate service classes for operations
- **Benefit**: Entities remain simple POJOs, easier to test and maintain

### 4. **Why Immutable Expense After Creation?**
- **Problem**: Changing expenses can cause inconsistent balances
- **Solution**: Make expenses immutable (or only allow specific updates)
- **Benefit**: Data consistency, easier to reason about state

### 5. **Why Split as Separate Entity?**
- **Problem**: Need to track individual shares with metadata
- **Solution**: Separate `Split` class instead of Map<User, Double>
- **Benefit**: Can add more metadata (paid status, notes) later

---

## Extensibility Points

### 1. **New Split Strategies**
```java
// Just implement the interface
public class CustomSplitStrategy implements ExpenseSplitStrategy {
    @Override
    public List<Split> calculateSplits(Expense expense, List<User> participants, Map<String, Object> metadata) {
        // Custom logic
    }
}
```

### 2. **Notification System**
- Add `NotificationService` interface
- Implement `EmailNotificationService`, `SMSNotificationService`
- Use Observer pattern to notify users of new expenses

### 3. **Currency Support**
- Add `Currency` enum or class
- Modify `Expense` and `Balance` to include currency
- Add `CurrencyConverter` service

### 4. **Expense Categories**
- Add `Category` enum (FOOD, TRAVEL, UTILITIES, etc.)
- Add category field to `Expense`
- Enable filtering and analytics by category

### 5. **Recurring Expenses**
- Add `RecurringExpense` class
- Add `RecurrencePattern` (daily, weekly, monthly)
- Add scheduler service to create expenses automatically

### 6. **Persistence Layer**
- Add Repository interfaces
- Implement with in-memory, database, or file storage
- Services depend on repository interfaces (DIP)

---

## Scalability Considerations

### 1. **Balance Simplification**
- Use graph algorithms to minimize number of transactions
- `BalanceManager.simplifyDebts()` reduces payment complexity

### 2. **Caching**
- Cache calculated balances
- Invalidate on new expense or payment

### 3. **Pagination**
- For large expense histories
- Add pagination to expense retrieval methods

### 4. **Indexing**
- Index users by ID and email
- Index expenses by date, group, user

---

## Testing Strategy

### 1. **Unit Tests**
- Test each split strategy independently
- Test balance calculations
- Test validation logic

### 2. **Integration Tests**
- Test complete expense creation flow
- Test balance updates across multiple expenses
- Test group operations

### 3. **Edge Cases**
- Zero amount expenses
- Single user expenses
- Rounding errors in splits
- Negative balances

---

## Example Usage Flow

```java
// 1. Create users
User alice = userService.createUser("Alice", "alice@example.com", "1234567890");
User bob = userService.createUser("Bob", "bob@example.com", "0987654321");
User charlie = userService.createUser("Charlie", "charlie@example.com", "1122334455");

// 2. Create a group
Group group = groupService.createGroup("Trip to Goa", List.of(alice, bob, charlie));

// 3. Create an expense with equal split
Expense expense1 = expenseService.createExpense(
    "Dinner",
    3000.0,
    alice,
    List.of(alice, bob, charlie),
    SplitType.EQUAL,
    group
);

// 4. Create an expense with exact split
Map<User, Double> exactAmounts = Map.of(
    alice, 500.0,
    bob, 1000.0,
    charlie, 1500.0
);
Expense expense2 = expenseService.createExpense(
    "Hotel",
    3000.0,
    bob,
    exactAmounts,
    SplitType.EXACT,
    group
);

// 5. Get balances
List<Balance> balances = balanceManager.getAllBalances();
Map<User, Double> aliceBalances = balanceManager.getUserBalances(alice);

// 6. Settle up
balanceManager.recordPayment(bob, alice, 500.0);
```

---

## Summary

This design follows all requested principles:
- ✅ **SOLID**: Each principle applied thoughtfully
- ✅ **DRY**: No code duplication
- ✅ **KISS**: Simple, clear design
- ✅ **YAGNI**: Only necessary features
- ✅ **Composition over Inheritance**: Strategy pattern, composition relationships
- ✅ **Modular**: Clear module boundaries
- ✅ **Extensible**: Easy to add features
- ✅ **Scalable**: Designed for growth

The design is production-ready and suitable for teaching LLD concepts.
