package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.ActionDispatcher
import kotlinx.coroutines.flow.StateFlow

interface Store<State> : ActionDispatcher {

    val state: StateFlow<State>
}

inline val <State> Store<State>.currentState get() = state.value