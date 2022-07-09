package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class MultiDispatch(val actions: List<Action>, val concurrent: Boolean = false): Thunk<Nothing?>({
    if (concurrent) coroutineScope { actions.forEach { launch { dispatch(it) } } }
    else actions.forEach { dispatch(it) }
})

operator fun Action.plus(other: Action) = MultiDispatch(unwrapped() + other.unwrapped())

infix fun MultiDispatch.concurrent(enabled: Boolean) = copy(concurrent = enabled)

private fun Action.unwrapped(): List<Action> = if (this is MultiDispatch) actions else listOf(this)
