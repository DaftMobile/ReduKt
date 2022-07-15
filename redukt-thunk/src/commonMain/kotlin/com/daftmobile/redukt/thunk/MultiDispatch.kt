package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

public data class MultiDispatch(val actions: List<Action>, val concurrent: Boolean = false): Thunk<Unit>({
    if (concurrent) coroutineScope { actions.forEach { launch { dispatch(it) } } }
    else actions.forEach { dispatch(it) }
})

public operator fun Action.plus(other: Action): MultiDispatch = MultiDispatch(unwrapped() + other.unwrapped())

public infix fun MultiDispatch.concurrent(enabled: Boolean): MultiDispatch = copy(concurrent = enabled)

private fun Action.unwrapped(): List<Action> = if (this is MultiDispatch) actions else listOf(this)
