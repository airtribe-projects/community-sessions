# Design Thought Process - Expense Sharing System

## My Step-by-Step Thinking Process

---

## Phase 1: Understanding the Problem Domain

### Initial Questions I Asked Myself

1. **What is the core problem?**
   - People share expenses and need to track who owes whom
   - Similar to Splitwise - a real-world, proven system

2. **What are the main use cases?**
   - Create an expense
   - Split it among multiple people
   - Track balances
   - Settle debts
   - Manage groups of people

3. **What makes this problem interesting from a design perspective?**
   - Multiple ways to split expenses (equal, exact amounts, percentages)
   - Need to handle complex balance calculations
   - Must be extensible for future features
   - Real-world constraints (money, rounding, consistency)

---

## Phase 2: Identifying Core Entities

### My Thought Process for Each Entity

#### 1. **User** - The Obvious Starting Point
```
Thinking: "Who are the actors in this system?"
→ People who share expenses
→ Need to identify them uniquely
→ Need contact information

Decision: Create User entity with id, name, email, phone
Principle Applied: SRP - User only stores user information
```

#### 2. **Expense** - The Central Concept
```
Thinking: "What is the main transaction in this system?"
→ Someone pays for something
→ Multiple people benefit from it
→ Need to track who paid and how much
→ Need to know how to split it

Decision: Create Expense entity with:
- Who paid (paidBy: User)
- How much (totalAmount: Double)
- What for (description: String)
- How to split (splitType: SplitType)
- When (timestamp: LocalDateTime)

Principle Applied: SRP - Expense stores expense data, not split logic
```

#### 3. **Split** - The Individual Share
```
Thinking: "How do I represent each person's share?"
→ Could use Map<User, Double> but that's limiting
→ What if I need to add more metadata later? (paid status, notes)
→ Better to have a dedicated class

Decision: Create Split entity with user and amount
Principle Applied: YAGNI initially, but designed for future extension
```

#### 4. **Group** - The Context
```
Thinking: "Expenses often happen in context of groups (trips, roommates)"
→ Need to group users together
→ Need to track group expenses
→ Groups can have multiple members and expenses

Decision: Create Group entity with members and expenses
Principle Applied: Composition - Group contains Users and Expenses
```

#### 5. **Balance** - The Derived State
```
Thinking: "How do I track who owes whom?"
→ Could calculate on-the-fly from expenses (expensive)
→ Could maintain a balance sheet (efficient)
→ Need to represent debt between two users

Decision: Create Balance entity with fromUser, toUser, amount
Principle Applied: Separation of concerns - Balance is separate from Expense
```

---

## Phase 3: Handling the Split Logic Challenge

### The Key Design Decision

```
Problem: Different ways to split expenses
- Equal: 3000 ÷ 3 = 1000 each
- Exact: Alice pays 500, Bob pays 1000, Charlie pays 1500
- Percentage: Alice 20%, Bob 30%, Charlie 50%

Initial Thought: "I could use if-else statements in Expense class"
```

```java
// ❌ BAD APPROACH - Violates OCP
public class Expense {
    public List<Split> calculateSplits() {
        if (splitType == SplitType.EQUAL) {
            // equal logic
        } else if (splitType == SplitType.EXACT) {
            // exact logic
        } else if (splitType == SplitType.PERCENTAGE) {
            // percentage logic
        }
        // Adding new type requires modifying this method!
    }
}
```

```
Problem with this approach:
1. Violates Open/Closed Principle (must modify for new types)
2. Violates Single Responsibility (Expense does too much)
3. Hard to test each split logic independently
4. Becomes messy with more split types

Better Approach: Strategy Pattern!
```

### Why Strategy Pattern?

```
Strategy Pattern Benefits:
1. Each split algorithm is a separate class (SRP)
2. Can add new strategies without modifying existing code (OCP)
3. Can swap strategies at runtime
4. Easy to test each strategy independently
5. Client code (ExpenseService) doesn't need to know implementation details (DIP)

Decision: Create ExpenseSplitStrategy interface
```

```java
// ✅ GOOD APPROACH - Strategy Pattern
public interface ExpenseSplitStrategy {
    List<Split> calculateSplits(Expense expense, List<User> participants, Map<String, Object> metadata);
    boolean validate(Expense expense, List<User> participants, Map<String, Object> metadata);
}

// Each strategy is independent and testable
public class EqualSplitStrategy implements ExpenseSplitStrategy { ... }
public class ExactSplitStrategy implements ExpenseSplitStrategy { ... }
public class PercentageSplitStrategy implements ExpenseSplitStrategy { ... }

// Adding new strategy? Just create new class!
public class CustomSplitStrategy implements ExpenseSplitStrategy { ... }
```

---

## Phase 4: Service Layer Design

### Why Not Put Logic in Entities?

```
Initial Thought: "Should Expense have createExpense() method?"

Problems with Entity-Heavy Design:
1. Entities become bloated with business logic
2. Hard to test (need to create full object graphs)
3. Violates SRP (entity manages data AND operations)
4. Tight coupling between entities

Better Approach: Service Layer
```

### Service Responsibilities

#### ExpenseService
```
Thinking: "What operations do I need for expenses?"
→ Create expense with different split types
→ Retrieve expenses by user, group, date
→ Coordinate between split strategies and balance updates

Decision: ExpenseService handles expense operations
- Depends on ExpenseSplitStrategy (interface, not concrete class) ← DIP
- Depends on BalanceManager for balance updates
- Maintains map of strategies for runtime selection
```

#### BalanceManager
```
Thinking: "Balance calculation is complex enough to deserve its own service"
→ Update balances when expense is created
→ Calculate net balances between users
→ Simplify debts (minimize transactions)
→ Record payments

Decision: Separate BalanceManager service
Principle Applied: SRP - Only handles balance logic
```

#### GroupService
```
Thinking: "Group operations are distinct from expense operations"
→ Create/manage groups
→ Add/remove members
→ Get group balances

Important Design Decision:
→ Should GroupService have addExpenseToGroup()?
→ NO - ExpenseService already handles this when creating expenses
→ Keeps responsibility in one place (SRP, DRY)

Decision: Separate GroupService for group management only
Principle Applied: SRP - Only handles group membership and metadata
```

#### UserService
```
Thinking: "User management is simple but should be separate"
→ Create users
→ Retrieve users by id/email

Decision: Simple UserService
Principle Applied: KISS - Keep it simple
```

---

## Phase 5: Applying SOLID Principles

### Single Responsibility Principle (SRP)

```
My Checklist for Each Class:
"Does this class have only ONE reason to change?"

✅ User - Changes only if user data structure changes
✅ Expense - Changes only if expense data structure changes
✅ Split - Changes only if split data structure changes
✅ Group - Changes only if group data structure changes
✅ Balance - Changes only if balance data structure changes
✅ ExpenseSplitStrategy - Changes only if split calculation logic changes
✅ ExpenseService - Changes only if expense operations change
✅ BalanceManager - Changes only if balance management logic changes
✅ GroupService - Changes only if group operations change
✅ UserService - Changes only if user operations change

Result: Each class has a single, well-defined responsibility
```

### Open/Closed Principle (OCP)

```
My Test: "Can I add new features without modifying existing code?"

Scenario: Add new split type "Share-Based Split"
(e.g., Alice gets 2 shares, Bob gets 1 share)

Without Strategy Pattern:
❌ Must modify Expense or ExpenseService
❌ Risk breaking existing split types
❌ Must retest everything

With Strategy Pattern:
✅ Create new ShareBasedSplitStrategy class
✅ Implement ExpenseSplitStrategy interface
✅ Register in ExpenseService
✅ No existing code modified!

Result: System is open for extension, closed for modification
```

### Liskov Substitution Principle (LSP)

```
My Test: "Can I substitute any strategy implementation?"

Code:
ExpenseSplitStrategy strategy = new EqualSplitStrategy();
// OR
ExpenseSplitStrategy strategy = new ExactSplitStrategy();
// OR
ExpenseSplitStrategy strategy = new PercentageSplitStrategy();

List<Split> splits = strategy.calculateSplits(expense, participants, metadata);

✅ All implementations can be used interchangeably
✅ All maintain the contract (return valid splits)
✅ No unexpected behavior

Result: Subtypes are truly substitutable
```

### Interface Segregation Principle (ISP)

```
My Test: "Are interfaces focused and minimal?"

ExpenseSplitStrategy interface:
- calculateSplits() - Essential
- validate() - Essential

✅ No unnecessary methods
✅ Clients only depend on what they need
✅ Small, focused interface

Alternative (BAD):
interface ExpenseSplitStrategy {
    List<Split> calculateSplits();
    void saveToDatabase();  // ❌ Not all strategies need this
    void sendNotification(); // ❌ Not all strategies need this
    String generateReport(); // ❌ Not all strategies need this
}

Result: Interfaces are segregated and focused
```

### Dependency Inversion Principle (DIP)

```
My Test: "Do high-level modules depend on abstractions?"

ExpenseService (high-level) depends on:
✅ ExpenseSplitStrategy (interface) - NOT concrete strategies
✅ BalanceManager (could be interface in future)

Benefits:
- Can swap strategy implementations
- Can mock for testing
- Loose coupling

Code Example:
public class ExpenseService {
    private Map<SplitType, ExpenseSplitStrategy> strategyMap;
    // Depends on interface, not concrete class!
    
    public ExpenseService(BalanceManager balanceManager) {
        this.balanceManager = balanceManager;
        // Register strategies (could use dependency injection)
        registerStrategy(SplitType.EQUAL, new EqualSplitStrategy());
        registerStrategy(SplitType.EXACT, new ExactSplitStrategy());
        registerStrategy(SplitType.PERCENTAGE, new PercentageSplitStrategy());
    }
}

Result: High-level modules depend on abstractions
```

---

## Phase 6: Composition Over Inheritance

### My Decision-Making Process

```
Question: "Should Group extend User collection?"

❌ BAD: Inheritance
class Group extends ArrayList<User> {
    // Group IS-A List of Users
}

Problems:
- Exposes all List methods (add, remove, clear) - may not want that
- Can't easily add more fields (expenses, name)
- Tight coupling to ArrayList implementation
- Hard to change later

✅ GOOD: Composition
class Group {
    private List<User> members;  // Group HAS-A List of Users
    private List<Expense> expenses;
    
    public void addMember(User user) { /* controlled access */ }
}

Benefits:
- Controlled interface
- Can change internal implementation
- Can add more fields easily
- Loose coupling
```

```
Question: "Should Expense contain split logic?"

❌ BAD: Inheritance
class EqualExpense extends Expense {
    // EqualExpense IS-A Expense
}
class ExactExpense extends Expense { }
class PercentageExpense extends Expense { }

Problems:
- Must create new class for each type
- Can't change split type after creation
- Violates OCP (must modify for new types)

✅ GOOD: Composition with Strategy
class Expense {
    private ExpenseSplitStrategy strategy;  // Expense HAS-A Strategy
}

Benefits:
- Single Expense class
- Can swap strategies
- Easy to add new strategies
- Follows OCP
```

---

## Phase 7: Ensuring Extensibility

### My Extensibility Checklist

```
1. New Split Types?
   ✅ Just implement ExpenseSplitStrategy
   ✅ No existing code changes needed

2. New Notification System?
   ✅ Create NotificationService interface
   ✅ Implement EmailNotification, SMSNotification
   ✅ Use Observer pattern to notify on events

3. Persistence Layer?
   ✅ Create Repository interfaces
   ✅ Implement InMemoryRepository, DatabaseRepository
   ✅ Services depend on repository interfaces (DIP)

4. Currency Support?
   ✅ Add Currency class
   ✅ Add to Expense and Balance
   ✅ Create CurrencyConverter service

5. Recurring Expenses?
   ✅ Create RecurringExpense class
   ✅ Add RecurrencePattern
   ✅ Create SchedulerService

Result: System is designed for growth
```

---

## Phase 8: Avoiding Common Pitfalls

### Pitfalls I Consciously Avoided

#### 1. God Class Anti-Pattern
```
❌ BAD: ExpenseManager does everything
class ExpenseManager {
    void createUser() { }
    void createGroup() { }
    void createExpense() { }
    void calculateBalances() { }
    void sendNotifications() { }
    void generateReports() { }
    // 1000+ lines of code
}

✅ GOOD: Separate services with clear responsibilities
```

#### 2. Primitive Obsession
```
❌ BAD: Using primitives everywhere
Map<String, Map<String, Double>> balances;  // What does this mean?

✅ GOOD: Create domain objects
class Balance {
    private User fromUser;
    private User toUser;
    private Double amount;
}
```

#### 3. Anemic Domain Model
```
❌ BAD: Entities with only getters/setters, all logic in services
class Expense {
    private String id;
    private Double amount;
    // Only getters/setters, no behavior
}

✅ BALANCED: Entities have some behavior, services have operations
class Expense {
    private String id;
    private Double amount;
    
    public boolean isValid() { return amount > 0; }
    public boolean involves(User user) { /* check if user in splits */ }
}
```

#### 4. Premature Optimization
```
❌ BAD: Over-engineering before needed
- Complex caching system
- Distributed architecture
- Message queues
- Microservices

✅ GOOD: Start simple, optimize when needed (YAGNI)
- In-memory storage initially
- Simple balance calculation
- Can add caching later if needed
```

---

## Phase 9: Design Trade-offs

### Trade-offs I Considered

#### 1. Immutability vs Flexibility
```
Trade-off: Should Expense be immutable?

Immutable (Chosen):
✅ Thread-safe
✅ Easier to reason about
✅ Prevents accidental modifications
❌ Can't edit expenses after creation

Mutable:
✅ Can edit expenses
❌ Risk of inconsistent state
❌ Must handle balance recalculation

Decision: Make Expense immutable for data consistency
```

#### 2. Eager vs Lazy Balance Calculation
```
Trade-off: When to calculate balances?

Eager (Chosen):
✅ Balances always up-to-date
✅ Fast retrieval
❌ Extra work on expense creation

Lazy:
✅ No work until needed
❌ Slow retrieval
❌ Must recalculate each time

Decision: Eager calculation for better user experience
```

#### 3. Simplified vs Detailed Balances
```
Trade-off: Should we simplify debts?

Simplified:
✅ Fewer transactions to settle
✅ Better user experience
❌ More complex algorithm

Detailed:
✅ Simple to implement
✅ Clear audit trail
❌ More transactions needed

Decision: Offer both - detailed by default, simplified on demand
```

---

## Phase 10: Validation of Design

### My Validation Checklist

```
✅ Can I explain each class's purpose in one sentence?
✅ Can I add new features without modifying existing code?
✅ Can I test each component independently?
✅ Are dependencies pointing inward (DIP)?
✅ Is each class focused on one responsibility (SRP)?
✅ Can I swap implementations easily?
✅ Is the code readable and maintainable?
✅ Does it follow industry best practices?
✅ Is it suitable for teaching LLD concepts?
✅ Would I be proud to show this in a code review?

Result: Design passes all validation checks
```

---

## Key Insights from This Design Process

### 1. **Start with Domain Understanding**
   - Don't jump to code immediately
   - Understand the problem deeply
   - Identify core concepts and relationships

### 2. **Think in Terms of Responsibilities**
   - Each class should have ONE clear job
   - If you can't explain it in one sentence, it's doing too much

### 3. **Design for Change**
   - Assume requirements will change
   - Make it easy to add features
   - Use interfaces and abstractions

### 4. **Favor Composition**
   - "HAS-A" is more flexible than "IS-A"
   - Avoid deep inheritance hierarchies
   - Use design patterns (Strategy, Factory, etc.)

### 5. **Keep It Simple**
   - Don't over-engineer
   - Add complexity only when needed
   - YAGNI is your friend

### 6. **Think About Testing**
   - If it's hard to test, it's poorly designed
   - Dependency injection enables testing
   - Each component should be testable in isolation

### 7. **Document Your Decisions**
   - Explain WHY, not just WHAT
   - Future you (or others) will thank you
   - Design documents are valuable

---

## Teaching Points for Students

### What Makes This Design Good?

1. **Clear Structure**: Easy to understand the system at a glance
2. **Extensible**: Can add features without breaking existing code
3. **Testable**: Each component can be tested independently
4. **Maintainable**: Easy to find and fix bugs
5. **Scalable**: Can handle growth in users and data
6. **Professional**: Follows industry best practices

### Common Mistakes to Avoid

1. ❌ Putting all logic in one class
2. ❌ Using if-else for different behaviors (use Strategy pattern)
3. ❌ Making everything public
4. ❌ Ignoring SOLID principles
5. ❌ Over-engineering (adding unnecessary complexity)
6. ❌ Under-engineering (not thinking about extensibility)

### How to Approach LLD Problems

1. **Understand** the problem domain
2. **Identify** core entities and relationships
3. **Apply** design principles (SOLID, DRY, KISS, YAGNI)
4. **Choose** appropriate design patterns
5. **Validate** your design against requirements
6. **Iterate** and refine

---

## Conclusion

This design is the result of systematic thinking, applying proven principles, and making conscious trade-offs. It's not perfect (no design is), but it's:

- ✅ **Solid** (pun intended)
- ✅ **Extensible**
- ✅ **Maintainable**
- ✅ **Testable**
- ✅ **Professional**
- ✅ **Educational**

Most importantly, it demonstrates how to think about software design, not just what the final result looks like.
