package dev.redukt.core

import dev.redukt.core.middleware.MiddlewareScope

class MockMiddleware<State>(
    var dispatchFunction: MiddlewareScope<State>.(Action) -> Unit = { }
) {

    var onCreate: MiddlewareScope<State>.() -> Unit = { }
    var lastReceivedAction: Action? = null
    var lastReceivedState: State? = null
    var wasCalled = false
    var wasCreated = false

    fun call(scope: MiddlewareScope<State>): DispatchFunction {
        onCreate(scope)
        wasCreated = true
        return {
            lastReceivedAction = it
            lastReceivedState = scope.currentState
            wasCalled = true
            dispatchFunction(scope, it)
        }
    }
}