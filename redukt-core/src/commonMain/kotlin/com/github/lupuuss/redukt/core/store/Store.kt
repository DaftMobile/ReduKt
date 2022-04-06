package com.github.lupuuss.redukt.core.store

import com.github.lupuuss.redukt.core.ActionDispatcher
import kotlinx.coroutines.flow.StateFlow

interface Store<State> : ActionDispatcher {

    val state: StateFlow<State>
}

inline val <State> Store<State>.currentState get() = state.value