package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

public typealias Selector<T, R> = (T) -> R

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector].
 *
 * Underlying implementation uses [stateIn] operator with [coroutineScope], [started] and initial state mapped with [selector].
 *
 */
public fun <State, SubState> Store<State>.select(
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    selector: Selector<State, SubState>
): StateFlow<SubState> = state
    .map(selector)
    .stateIn(coroutineScope, started, selector(state.value))