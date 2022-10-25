package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

/**
 * The scope for accessing current state of the Redux store and dispatching actions. This is set of actions available from a middleware.
 */
public interface DispatchScope<out State> {

    public val closure: DispatchClosure

    public val currentState: State

    public fun dispatch(action: Action)
}

public fun DispatchScope<*>.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}
