package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope

data class DispatchList(val actions: List<Action>) : ExecuteThunk<Nothing?, Unit>() {
    override fun DispatchScope<Nothing?>.execute() = actions.forEach(::dispatch)
}

operator fun Action.plus(action: Action) = DispatchList(this.unwrapped() + action.unwrapped())

private fun Action.unwrapped() = if (this is DispatchList) actions else listOf(this)

fun thunkOf(action: Action): Thunk<Unit> = DispatchList(listOf(action))

fun thunkOf(vararg actions: Action): Thunk<Unit> = DispatchList(actions.toList())