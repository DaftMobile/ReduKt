package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.test.middleware.MiddlewareTestScope
import com.daftmobile.redukt.test.middleware.MiddlewareTester
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> MiddlewareTester<T>.runCoroutineTest(block: suspend MiddlewareTestScope<T>.() -> Unit) = test {
    runTest {
        block()
    }
}

fun <State> dispatchScope(
    closure: DispatchClosure = EmptyDispatchClosure,
    dispatch: DispatchFunction = { },
    getState: () -> State,
): DispatchScope<State> = object : DispatchScope<State> {
    override val closure: DispatchClosure = closure
    override val currentState: State get() = getState()
    override fun dispatch(action: Action) = dispatch(action)
}
