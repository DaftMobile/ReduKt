package com.daftmobile.redukt.thunk.combine

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.Thunk

data class PlusCombined(val actions: List<Action>) : Thunk.Executable<Nothing> {
    override fun DispatchScope<Nothing>.execute() = actions.forEach(::dispatch)
}

operator fun Action.plus(action: Action) = PlusCombined(this.unwrapped() + action.unwrapped())

private fun Action.unwrapped() = if (this is PlusCombined) actions else listOf(this)