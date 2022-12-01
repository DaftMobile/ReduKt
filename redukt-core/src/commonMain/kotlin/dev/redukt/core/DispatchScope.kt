package dev.redukt.core

import dev.redukt.core.closure.DispatchClosure

/**
 * The scope for accessing current state of the Redux store and dispatching actions.
 * Beside standard Redux operations, it introduces a [DispatchClosure] mechanism.
 */
public interface DispatchScope<out State> {

    /**
     * There is no equivalent to this in original Redux. It provides a current [DispatchClosure].
     * It remains immutable. However, elements inside might mutate depending on their implementation.
     */
    public val closure: DispatchClosure

    /**
     * This is equivalent to [Redux getState](https://redux.js.org/api/store#getState).
     */
    public val currentState: State

    /**
     * This is equivalent to [Redux dispatch](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#dispatch).
     */
    public fun dispatch(action: Action)
}

public fun DispatchScope<*>.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}
