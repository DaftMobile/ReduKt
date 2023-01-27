package com.daftmobile.redukt.test

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.closure.DispatchClosure

/**
 * The scope for mutating [com.daftmobile.redukt.core.DispatchScope] behaviour in tests.
 */
public interface MutableDispatchScope<State> {

    public var currentState: State

    public var closure: DispatchClosure

    /**
     * Sets a [block] that will be called on every dispatch called by component under test.
     * Each call replaces previously provided [block].
     */
    public fun onDispatch(block: MutableDispatchScope<State>.(Action) -> Unit)

}