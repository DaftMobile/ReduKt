package com.daftmobile.redukt.core.store.select

import kotlinx.coroutines.flow.StateFlow

internal class NewSelectStateFlowProvider : SelectStateFlowProvider {
    override fun <State, Selected> provide(
        state: StateFlow<State>,
        selector: Selector<State, Selected>
    ): StateFlow<Selected> = SelectStateFlow(state, selector)
}
