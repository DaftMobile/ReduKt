package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

public interface DispatchScope<State> {

    public val closure: DispatchClosure

    public val currentState: State

    public fun dispatch(action: Action)
}

public fun DispatchScope<*>.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}
