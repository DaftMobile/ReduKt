package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.coroutines.skipCoroutinesClosure
import com.daftmobile.redukt.core.store.select.SelectStateFlowProvider
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Creates a [Store] simplified store implementation for UI previews.
 */
public fun <State> previewStore(
    initialState: State,
    initialClosure: DispatchClosure = EmptyDispatchClosure,
    reducer: Reducer<State> = { state, _ -> state }
): Store<State> = PreviewStore(initialState, initialClosure, reducer)

private class PreviewStore<State>(
    initialState: State,
    initialClosure: DispatchClosure,
    private val reducer: Reducer<State>
) : Store<State> {

    override val state = MutableStateFlow(initialState)
    override val closure: DispatchClosure = skipCoroutinesClosure() + SelectStateFlowProvider() + initialClosure
    override val currentState: State = state.value

    override fun dispatch(action: Action) {
        state.value = reducer(state.value, action)
    }
}
