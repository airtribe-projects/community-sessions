package com.airtribe.vendingmachine.state;

import com.airtribe.vendingmachine.VendingMachine;
import java.util.HashMap;
import java.util.Map;

public class StateManager {
    private final Map<StateType, State> states;

    public StateManager(VendingMachine machine) {
        this.states = new HashMap<>();
        registerState(StateType.IDLE, new IdleState(machine));
        registerState(StateType.HAS_MONEY, new HasMoneyState(machine));
        registerState(StateType.DISPENSE, new DispenseState(machine));
        registerState(StateType.REFUND, new RefundState(machine));
    }

    /**
     * Register a new state. Can be used to add custom states without modifying manager.
     */
    public void registerState(StateType type, State state) {
        states.put(type, state);
    }

    /**
     * Get a state by type.
     */
    public State getState(StateType type) {
        State state = states.get(type);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state type: " + type);
        }
        return state;
    }

    /**
     * Check if a state type is registered.
     */
    public boolean hasState(StateType type) {
        return states.containsKey(type);
    }
}