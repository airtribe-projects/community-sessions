# Vending Machine System

A low-level design implementation of a vending machine system following SOLID principles.

## Features

1. **Inventory Management** - Add, update, and track product stock
2. **Product Dispensing** - Dispense products when conditions are met
3. **Payment Collection** - Accept coins and notes, calculate change
4. **Transaction Cancellation** - Refund money and cancel transactions

## Design Principles

### SOLID Principles Applied

- **Single Responsibility Principle (SRP)**: Each class has one responsibility
  - `Product`: Product data
  - `Inventory`: Stock management
  - `Payment`: Payment handling
  - `State`: State behavior
  - `VendingMachine`: Orchestration

- **Open/Closed Principle (OCP)**: Classes are open for extension, closed for modification
  - New states can be added without modifying existing states
  - New payment methods can be added by extending the payment system

- **Liskov Substitution Principle (LSP)**: All state implementations can substitute the base `State` class

- **Interface Segregation Principle (ISP)**: Small, focused interfaces for specific behaviors

- **Dependency Inversion Principle (DIP)**: Depends on abstractions (State) not concrete implementations

### Design Patterns

- **State Pattern**: Manages vending machine states (Idle, HasMoney, Dispense, Refund)
- **Enum Pattern**: For Coins and Notes with fixed denominations

## Project Structure

```
src/main/java/com/airtribe/vendingmachine/
├── VendingMachine.java          # Main orchestrator
├── Main.java                    # Demo application
├── model/
│   ├── Product.java            # Product entity
│   ├── Coin.java               # Coin denominations
│   └── Note.java               # Note denominations
├── inventory/
│   └── Inventory.java          # Inventory management
├── payment/
│   └── Payment.java            # Payment handling
├── state/
│   ├── State.java              # Abstract state
│   ├── IdleState.java          # Waiting for selection
│   ├── HasMoneyState.java      # Accepting payment
│   ├── DispenseState.java      # Dispensing product
│   └── RefundState.java        # Refunding money
└── exception/
    ├── ProductNotFoundException.java
    ├── InsufficientStockException.java
    └── InsufficientPaymentException.java
```

## How to Run

### Using Maven:

```bash
# Compile
mvn clean compile

# Run
mvn exec:java
```

### Using Java directly:

```bash
# Compile
javac -d target/classes src/main/java/com/airtribe/vendingmachine/**/*.java

# Run
java -cp target/classes com.airtribe.vendingmachine.Main
```

## State Transitions

```
IdleState
   ↓ (selectProduct)
HasMoneyState
   ↓ (insertMoney → sufficient)
DispenseState
   ↓ (dispense complete)
IdleState

OR

HasMoneyState
   ↓ (cancelTransaction)
RefundState
   ↓ (refund complete)
IdleState
```

## Example Usage

```java
VendingMachine vm = new VendingMachine();

// Add products
vm.addProduct(new Product("A1", "Coke", 1.50), 5);

// Buy a product
vm.selectProduct("A1");
vm.insertNote(Note.FIVE);
// Product dispensed, change returned

// Cancel transaction
vm.selectProduct("A1");
vm.insertCoin(Coin.QUARTER);
vm.cancelTransaction();
// Money refunded
```

## Extensibility

The system is designed to be easily extensible:

1. **Add new states**: Extend `State` class
2. **Add new payment methods**: Extend payment system
3. **Add new coin/note denominations**: Update enums
4. **Add new features**: Create new classes following SRP

## Thread Safety

The `Inventory` class uses `synchronized` methods to ensure thread-safe operations in a multi-threaded environment.
