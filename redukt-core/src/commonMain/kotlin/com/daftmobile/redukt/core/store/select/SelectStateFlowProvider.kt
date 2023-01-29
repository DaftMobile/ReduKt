package com.daftmobile.redukt.core.store.select

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.flow.StateFlow

@DelicateReduKtApi
public interface SelectStateFlowProvider : DispatchClosure.Element {

    override val key: Key get() = Key

    public fun <State, Selected> provide(
        state: StateFlow<State>,
        selector: Selector<State, Selected>
    ): StateFlow<Selected>

    public companion object Key : DispatchClosure.Key<SelectStateFlowProvider> {

        public operator fun invoke(): SelectStateFlowProvider = NewSelectStateFlowProvider()
    }
}
