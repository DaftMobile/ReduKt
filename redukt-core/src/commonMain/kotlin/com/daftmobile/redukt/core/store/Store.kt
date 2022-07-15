package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.ActionDispatcher
import kotlinx.coroutines.flow.StateFlow

public interface Store<State> : ActionDispatcher {

    public val state: StateFlow<State>
}

public inline val <State> Store<State>.currentState: State get() = state.value