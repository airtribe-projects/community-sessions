# Vending Machine Class Diagram

## UML Class Diagram

```mermaid
classDiagram
    %% Main Orchestrator
    class VendingMachine {
        -Inventory inventory
        -Payment payment
        -DisplayService displayService
        -VendingMachineContext context
        -State idleState
        -State hasMoneyState
        -State dispenseState
        -State refundState
        +VendingMachine()
        +selectProduct(String)
        +insertCoin(Coin)
        +insertNote(Note)
        +cancelTransaction()
        +addProduct(Product, int)
        +updateStock(String, int)
        +displayInventory()
        +displayStatus()
    }

    %% Context
    class VendingMachineContext {
        -Inventory inventory
        -Payment payment
        -DisplayService displayService
        -State currentState
        -String selectedProduct
        -State idleState
        -State hasMoneyState
        -State dispenseState
        -State refundState
        +setState(State)
        +getCurrentState() State
        +setSelectedProduct(String)
        +getSelectedProduct() String
        +getProduct(String) Product
        +isProductAvailable(String) boolean
        +dispenseProduct(String)
        +getTotalPayment() double
        +isPaymentSufficient(double) boolean
        +calculateChange(double) double
        +resetPayment()
        +getDisplay() DisplayService
    }

    %% State Pattern
    class State {
        <<abstract>>
        #VendingMachineContext context
        +State(VendingMachineContext)
        +selectProduct(String)*
        +insertCoin(Coin)*
        +insertNote(Note)*
        +cancelTransaction()*
        +dispenseProduct()*
        +refund()*
        +getStateName()* String
    }

    class IdleState {
        +IdleState(VendingMachineContext)
        +selectProduct(String)
        +insertCoin(Coin)
        +insertNote(Note)
        +cancelTransaction()
        +dispenseProduct()
        +refund()
        +getStateName() String
    }

    class HasMoneyState {
        -Payment payment
        +HasMoneyState(VendingMachineContext, Payment)
        +selectProduct(String)
        +insertCoin(Coin)
        +insertNote(Note)
        +cancelTransaction()
        +dispenseProduct()
        +refund()
        +getStateName() String
        -checkPaymentAndDispense()
    }

    class DispenseState {
        +DispenseState(VendingMachineContext)
        +selectProduct(String)
        +insertCoin(Coin)
        +insertNote(Note)
        +cancelTransaction()
        +dispenseProduct()
        +refund()
        +getStateName() String
    }

    class RefundState {
        +RefundState(VendingMachineContext)
        +selectProduct(String)
        +insertCoin(Coin)
        +insertNote(Note)
        +cancelTransaction()
        +dispenseProduct()
        +refund()
        +getStateName() String
    }

    %% Domain Models
    class Product {
        -String code
        -String name
        -double price
        +Product(String, String, double)
        +getCode() String
        +getName() String
        +getPrice() double
        +equals(Object) boolean
        +hashCode() int
    }

    class Coin {
        <<enumeration>>
        PENNY
        NICKEL
        DIME
        QUARTER
        HALF_DOLLAR
        DOLLAR
        -double value
        +getValue() double
    }

    class Note {
        <<enumeration>>
        ONE
        FIVE
        TEN
        TWENTY
        -double value
        +getValue() double
    }

    %% Core Services
    class Inventory {
        -Map~String, Integer~ productStock
        -Map~String, Product~ products
        +addProduct(Product, int)
        +updateStock(String, int)
        +isAvailable(String) boolean
        +getStock(String) int
        +getProduct(String) Product
        +dispenseProduct(String)
        +getAllProducts() Map
        +getStockInfo() Map
    }

    class Payment {
        -double totalAmount
        -List~Coin~ coinsInserted
        -List~Note~ notesInserted
        +insertCoin(Coin)
        +insertNote(Note)
        +getTotalAmount() double
        +isSufficient(double) boolean
        +calculateChange(double) double
        +reset()
        +getCoinsInserted() List
        +getNotesInserted() List
    }

    class DisplayService {
        +showProductSelected(String)
        +showOutOfStock()
        +showInsertMoney(double)
        +showCoinInserted(double, double)
        +showNoteInserted(double, double)
        +showInsufficientPayment(double)
        +showPaymentSufficient()
        +showDispensingProduct(String)
        +showReturningChange(double)
        +showThankYou()
        +showTransactionCancelled()
        +showRefunding(double)
        +showNoMoneyToRefund()
        +showTransactionComplete()
        +showError(String)
        +showInvalidOperation(String)
        +showInventory(Map, Map)
        +showStatus(String, String, double)
        +showProductAdded(int, String, double)
        +showStockUpdated(String)
    }

    %% Exceptions
    class ProductNotFoundException {
        <<exception>>
        +ProductNotFoundException(String)
    }

    class InsufficientStockException {
        <<exception>>
        +InsufficientStockException(String)
    }

    class InsufficientPaymentException {
        <<exception>>
        +InsufficientPaymentException(String)
    }

    %% Relationships
    VendingMachine --> Inventory : has
    VendingMachine --> Payment : has
    VendingMachine --> DisplayService : has
    VendingMachine --> VendingMachineContext : has
    VendingMachine --> State : references

    VendingMachineContext --> Inventory : uses
    VendingMachineContext --> Payment : uses
    VendingMachineContext --> DisplayService : uses
    VendingMachineContext --> State : manages

    State <|-- IdleState : extends
    State <|-- HasMoneyState : extends
    State <|-- DispenseState : extends
    State <|-- RefundState : extends

    State --> VendingMachineContext : uses

    Inventory --> Product : manages
    Inventory ..> ProductNotFoundException : throws
    Inventory ..> InsufficientStockException : throws

    Payment --> Coin : accepts
    Payment --> Note : accepts
    Payment ..> InsufficientPaymentException : may throw

    HasMoneyState --> Payment : uses directly
```

## Simplified Component Diagram

```mermaid
graph TB
    subgraph "Vending Machine System"
        VM[VendingMachine<br/>Orchestrator]
        
        subgraph "Core Components"
            INV[Inventory<br/>Stock Management]
            PAY[Payment<br/>Money Handling]
            DISP[DisplayService<br/>UI Output]
        end
        
        subgraph "State Management"
            CTX[VendingMachineContext<br/>Controlled Access]
            
            subgraph "States"
                IDLE[IdleState]
                MONEY[HasMoneyState]
                DISPENSE[DispenseState]
                REFUND[RefundState]
            end
        end
        
        subgraph "Models"
            PROD[Product]
            COIN[Coin]
            NOTE[Note]
        end
    end
    
    VM --> INV
    VM --> PAY
    VM --> DISP
    VM --> CTX
    
    CTX --> INV
    CTX --> PAY
    CTX --> DISP
    
    CTX --> IDLE
    CTX --> MONEY
    CTX --> DISPENSE
    CTX --> REFUND
    
    INV --> PROD
    PAY --> COIN
    PAY --> NOTE
    
    MONEY --> PAY
    
    style VM fill:#e1f5ff
    style CTX fill:#fff4e6
    style DISP fill:#f3e5f5
    style INV fill:#e8f5e9
    style PAY fill:#fff3e0
```

## Design Patterns Used

### 1. **State Pattern** 🔄
- **Context**: `VendingMachineContext`
- **Abstract State**: `State`
- **Concrete States**: `IdleState`, `HasMoneyState`, `DispenseState`, `RefundState`

### 2. **Facade Pattern** 🎭
- **Facade**: `VendingMachine`
- Provides simplified interface to complex subsystem

### 3. **Strategy Pattern** 💡
- Different states encapsulate different behaviors
- Runtime state switching changes behavior

## Key Relationships

| Relationship | Type | Description |
|-------------|------|-------------|
| VendingMachine → Components | **Composition** | Owns Inventory, Payment, DisplayService |
| VendingMachine → Context | **Composition** | Creates and manages context |
| State → VendingMachineContext | **Association** | Uses context for operations |
| ConcreteState → State | **Inheritance** | Extends abstract state |
| Inventory → Product | **Aggregation** | Manages collection of products |
| Payment → Coin/Note | **Association** | Accepts and tracks money |

## Dependency Flow

```
User Input
    ↓
VendingMachine (Facade)
    ↓
VendingMachineContext
    ↓
Current State (polymorphism)
    ↓
├─→ Inventory (stock operations)
├─→ Payment (money operations)
└─→ DisplayService (output)
```

## SOLID Principles Illustrated

- **SRP**: Each class has one responsibility
  - `DisplayService` → UI only
  - `Inventory` → Stock only
  - `Payment` → Money only
  
- **OCP**: Open for extension
  - New states can be added
  - DisplayService can be swapped
  
- **LSP**: States are substitutable
  - All states implement State interface
  
- **ISP**: Focused interfaces
  - Context provides specific methods
  
- **DIP**: Depend on abstractions
  - States depend on Context, not VendingMachine
