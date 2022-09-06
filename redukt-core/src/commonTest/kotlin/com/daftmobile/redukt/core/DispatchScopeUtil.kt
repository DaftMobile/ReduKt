package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure

fun <State> dispatchScope(
    closure: DispatchClosure = EmptyDispatchClosure,
    dispatch: DispatchFunction = { },
    getState: () -> State,
): DispatchScope<State> = object : DispatchScope<State> {
    override val closure: DispatchClosure = closure
    override val currentState: State get() = getState()
    override fun dispatch(action: Action) = dispatch(action)
}
