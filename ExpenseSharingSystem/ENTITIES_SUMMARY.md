# Entities Summary - Expense Sharing System

## Quick Reference Guide

---

## Core Entities (10 Total)

### 1. **User** 👤
**Purpose**: Represents a person in the system

**Attributes**:
- `id: String` - Unique identifier
- `name: String` - User's full name
- `email: String` - Email address (unique)
- `phone: String` - Phone number

**Key Methods**:
- `getId()`, `getName()`, `getEmail()`, `getPhone()`
- `equals()`, `hashCode()` - For collections and comparisons

**Relationships**:
- Many-to-many with `Group`
- One-to-many with `Expense` (as payer)
- Referenced by `Split`
- Referenced by `Balance`

**SOLID Principle**: SRP - Only manages user data

---

### 2. **Expense** 💰
**Purpose**: Represents a single expense transaction

**Attributes**:
- `id: String` - Unique identifier
- `description: String` - What the expense is for
- `totalAmount: Double` - Total amount paid
- `paidBy: User` - Who paid for this expense
- `splits: List<Split>` - How the expense is divided
- `splitType: SplitType` - Type of split (EQUAL, EXACT, PERCENTAGE)
- `group: Group` - Optional group this expense belongs to
- `timestamp: LocalDateTime` - When expense was created

**Key Methods**:
- Getters for all attributes
- `isValid()` - Validates expense data
- `involves(User)` - Checks if user is part of this expense

**Relationships**:
- Composition with `Split` (owns splits)
- References `User` (paidBy)
- References `SplitType`
- References `Group` (optional)

**Design Decision**: Immutable after creation for data consistency

**SOLID Principle**: SRP - Only stores expense data, not split logic

---

### 3. **Split** 📊
**Purpose**: Represents an individual's share in an expense

**Attributes**:
- `user: User` - Who owes this amount
- `amount: Double` - How much they owe
- `percentage: Double` - Optional percentage (for percentage splits)

**Key Methods**:
- `getUser()`, `getAmount()`, `getPercentage()`
- `setAmount(Double)` - Update amount (used during calculation)

**Relationships**:
- Many-to-one with `User`
- Owned by `Expense`

**Why Separate Class?**:
- Could have used `Map<User, Double>` but separate class allows:
  - Adding more metadata later (paid status, notes)
  - Better type safety
  - Clearer semantics

**SOLID Principle**: SRP - Only stores split information

---

### 4. **Group** 👥
**Purpose**: Represents a collection of users who share expenses

**Attributes**:
- `id: String` - Unique identifier
- `name: String` - Group name (e.g., "Trip to Goa")
- `members: List<User>` - Users in this group
- `expenses: List<Expense>` - Expenses in this group
- `createdAt: LocalDateTime` - When group was created

**Key Methods**:
- Getters for all attributes
- `addMember(User)` - Add user to group
- `removeMember(User)` - Remove user from group
- `addExpense(Expense)` - Add expense to group
- `isMember(User)` - Check if user is in group

**Relationships**:
- Composition with `User` (contains members)
- Composition with `Expense` (contains expenses)

**Design Decision**: Composition over inheritance (HAS-A, not IS-A)

**SOLID Principle**: SRP - Only manages group data and membership

---

### 5. **Balance** ⚖️
**Purpose**: Represents amount owed between two users

**Attributes**:
- `fromUser: User` - Who owes money
- `toUser: User` - Who is owed money
- `amount: Double` - How much is owed

**Key Methods**:
- `getFromUser()`, `getToUser()`, `getAmount()`
- `setAmount(Double)` - Update balance amount
- `toString()` - Human-readable representation

**Relationships**:
- Two many-to-one relationships with `User`

**Why Separate from Expense?**:
- Balances are derived state across multiple expenses
- Need to track net balances
- Can be simplified/optimized independently

**SOLID Principle**: SRP - Only stores balance information

---

### 6. **SplitType** (Enum) 🔢
**Purpose**: Defines the type of expense split

**Values**:
- `EQUAL` - Split equally among all participants
- `EXACT` - Each person pays exact specified amount
- `PERCENTAGE` - Each person pays specified percentage

**Usage**:
- Used by `Expense` to indicate split type
- Used by `ExpenseService` to select appropriate strategy

**Extensibility**: Can add new types (e.g., SHARE_BASED, RATIO)

---

## Strategy Interfaces

### 7. **ExpenseSplitStrategy** (Interface) 🎯
**Purpose**: Defines contract for split calculation algorithms

**Methods**:
- `calculateSplits(Expense, List<User>, Map<String, Object>): List<Split>`
  - Calculates how expense should be split
  - Returns list of Split objects
  
- `validate(Expense, List<User>, Map<String, Object>): boolean`
  - Validates if split is possible with given data
  - Returns true if valid

**Implementations**:
- `EqualSplitStrategy`
- `ExactSplitStrategy`
- `PercentageSplitStrategy`

**Design Pattern**: Strategy Pattern

**SOLID Principles**:
- OCP - Open for extension (new strategies), closed for modification
- LSP - All implementations are substitutable
- ISP - Small, focused interface
- DIP - High-level code depends on this abstraction

---

## Service Classes

### 8. **ExpenseService** 🔧
**Purpose**: Manages expense operations and coordinates split strategies

**Attributes**:
- `expenses: List<Expense>` - All expenses in system
- `balanceManager: BalanceManager` - For balance updates
- `strategyMap: Map<SplitType, ExpenseSplitStrategy>` - Strategy registry

**Key Methods**:
- `createExpense(...)` - Create expense with equal split
- `createExpenseWithExactAmounts(...)` - Create with exact amounts
- `createExpenseWithPercentages(...)` - Create with percentages
- `getExpenseById(String)` - Retrieve expense by ID
- `getExpensesByUser(User)` - Get all expenses for a user
- `getExpensesByGroup(Group)` - Get all expenses in a group

**Responsibilities**:
- Create and manage expenses
- Select and apply appropriate split strategy
- Coordinate with BalanceManager for balance updates
- Validate expense data

**Dependencies**:
- `ExpenseSplitStrategy` (interface) - For split calculation
- `BalanceManager` - For balance updates

**SOLID Principles**:
- SRP - Only handles expense operations
- OCP - Can add new split types without modification
- DIP - Depends on ExpenseSplitStrategy interface

---

### 9. **BalanceManager** 💼
**Purpose**: Manages balance calculations and debt tracking

**Attributes**:
- `balances: Map<String, Map<String, Double>>` - Nested map of user balances

**Key Methods**:
- `updateBalances(Expense)` - Update balances when expense is created
- `getUserBalances(User)` - Get all balances for a user
- `getAllBalances()` - Get all balances in system
- `getBalance(User, User)` - Get balance between two users
- `recordPayment(User, User, Double)` - Record a payment/settlement
- `simplifyDebts()` - Minimize number of transactions needed

**Responsibilities**:
- Track who owes whom
- Calculate net balances
- Simplify debts using graph algorithms
- Record settlements

**Balance Representation**:
```
balances = {
  "user1": {
    "user2": 500.0,   // user1 owes user2 500
    "user3": -200.0   // user3 owes user1 200
  }
}
```

**SOLID Principle**: SRP - Only handles balance management

---

### 10. **GroupService** 👨‍👩‍👧‍👦
**Purpose**: Manages group operations

**Attributes**:
- `groups: List<Group>` - All groups in system
- `expenseService: ExpenseService` - For expense operations

**Key Methods**:
- `createGroup(String, List<User>)` - Create new group
- `getGroupById(String)` - Retrieve group by ID
- `addMemberToGroup(String, User)` - Add user to group
- `removeMemberFromGroup(String, User)` - Remove user from group
- `addExpenseToGroup(String, Expense)` - Add expense to group
- `getGroupBalances(String)` - Get balances within group

**Responsibilities**:
- Create and manage groups
- Manage group membership
- Coordinate group expenses
- Calculate group-specific balances

**Dependencies**:
- `ExpenseService` - For expense-related operations

**SOLID Principle**: SRP - Only handles group operations

---

### 11. **UserService** 👨‍💼
**Purpose**: Manages user operations

**Attributes**:
- `users: Map<String, User>` - Users indexed by ID
- `emailIndex: Map<String, User>` - Users indexed by email

**Key Methods**:
- `createUser(String, String, String)` - Create new user
- `getUserById(String)` - Retrieve user by ID
- `getUserByEmail(String)` - Retrieve user by email
- `getAllUsers()` - Get all users

**Responsibilities**:
- Create and manage users
- Maintain user indices for fast lookup
- Ensure email uniqueness

**SOLID Principle**: SRP - Only handles user operations

---

## Entity Relationships Summary

### Composition (Strong Ownership) ◆──→
- `Expense` ◆──→ `Split` - Expense owns its splits
- `Group` ◆──→ `User` - Group contains users
- `Group` ◆──→ `Expense` - Group contains expenses

### Association (Reference) ──→
- `Split` ──→ `User` - Split references user
- `Expense` ──→ `User` - Expense references payer
- `Expense` ──→ `SplitType` - Expense uses split type
- `Expense` ──→ `Group` - Expense belongs to group
- `Balance` ──→ `User` (x2) - Balance references two users

### Dependency (Uses) ···→
- `ExpenseService` ···→ `ExpenseSplitStrategy` - Uses for calculation
- `ExpenseService` ···→ `BalanceManager` - Uses for balance updates
- `GroupService` ···→ `ExpenseService` - Uses for expense operations

### Implementation (Realizes) ──▷
- `EqualSplitStrategy` ──▷ `ExpenseSplitStrategy`
- `ExactSplitStrategy` ──▷ `ExpenseSplitStrategy`
- `PercentageSplitStrategy` ──▷ `ExpenseSplitStrategy`

---

## Design Patterns Used

### 1. **Strategy Pattern**
- **Components**: `ExpenseSplitStrategy` interface + implementations
- **Purpose**: Select split algorithm at runtime
- **Benefit**: Easy to add new split types (OCP)

### 2. **Service Layer Pattern**
- **Components**: `ExpenseService`, `GroupService`, `UserService`, `BalanceManager`
- **Purpose**: Separate business logic from entities
- **Benefit**: Better testability, clear separation of concerns

### 3. **Composition Pattern**
- **Components**: `Group` contains `User` and `Expense`
- **Purpose**: Build complex objects from simpler ones
- **Benefit**: More flexible than inheritance

---

## SOLID Principles Mapping

| Principle | How Applied |
|-----------|-------------|
| **S**RP | Each class has single responsibility |
| **O**CP | Strategy pattern allows extension without modification |
| **L**SP | All strategy implementations are substitutable |
| **I**SP | Small, focused interfaces (ExpenseSplitStrategy) |
| **D**IP | Services depend on abstractions (interfaces) |

---

## Key Design Decisions

### 1. **Why Strategy Pattern for Splits?**
- ✅ Easy to add new split types
- ✅ Each strategy is independently testable
- ✅ Follows OCP
- ❌ Alternative: if-else statements would violate OCP

### 2. **Why Separate Balance Entity?**
- ✅ Balances are derived state, not raw data
- ✅ Can be calculated and simplified independently
- ✅ Clear separation of concerns
- ❌ Alternative: Calculate on-the-fly (expensive)

### 3. **Why Service Layer?**
- ✅ Entities remain simple POJOs
- ✅ Business logic is centralized
- ✅ Easier to test
- ❌ Alternative: Logic in entities (anemic domain model)

### 4. **Why Immutable Expenses?**
- ✅ Thread-safe
- ✅ Prevents inconsistent state
- ✅ Easier to reason about
- ❌ Alternative: Mutable (must handle balance recalculation)

### 5. **Why Composition Over Inheritance?**
- ✅ More flexible
- ✅ Avoids deep hierarchies
- ✅ Can change behavior at runtime
- ❌ Alternative: Inheritance (rigid, hard to change)

---

## Extensibility Points

### Easy to Add:
1. ✅ New split strategies (implement interface)
2. ✅ Notification system (new service)
3. ✅ Persistence layer (repository pattern)
4. ✅ Currency support (add to entities)
5. ✅ Expense categories (add enum)
6. ✅ Recurring expenses (new entity)
7. ✅ Payment methods (add enum)
8. ✅ Expense attachments (add to Expense)

---

## Summary Statistics

- **Total Entities**: 11 (5 core + 1 enum + 1 interface + 4 services)
- **Design Patterns**: 3 (Strategy, Service Layer, Composition)
- **SOLID Principles**: All 5 applied
- **Relationships**: 15+ (composition, association, dependency)
- **Extensibility Points**: 8+ identified

---

## Teaching Value

This design is excellent for teaching because it demonstrates:

1. ✅ Real-world problem solving
2. ✅ SOLID principles in action
3. ✅ Design patterns usage
4. ✅ Trade-off analysis
5. ✅ Extensible architecture
6. ✅ Clean code practices
7. ✅ Professional software design

Perfect for LLD interviews and system design discussions! 🎯
