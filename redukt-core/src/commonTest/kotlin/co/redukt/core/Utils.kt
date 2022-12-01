package co.redukt.core

import co.redukt.core.closure.DispatchClosure
import co.redukt.core.closure.EmptyDispatchClosure
import co.redukt.test.middleware.MiddlewareTestScope
import co.redukt.test.middleware.MiddlewareTester
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
