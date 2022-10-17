package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

/**
 * The scope for accessing current state of the [com.daftmobile.redukt.core.store.Store] and dispatching actions.
 */
public interface DispatchScope<State> {

    public val closure: DispatchClosure

    public val currentState: State

    public fun dispatch(action: Action)
}

public fun DispatchScope<*>.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}
