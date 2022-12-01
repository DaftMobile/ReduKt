package dev.redukt.core

class MockReducer<State>(var onReducerCall: (State, Action) -> State) {

    var receivedState: State? = null
    var receivedAction: Action? = null
    var wasCalled = false

    fun call(state: State, action: Action): State {
        receivedState = state
        receivedAction = action
        wasCalled = true
        return onReducerCall(state, action)
    }
}