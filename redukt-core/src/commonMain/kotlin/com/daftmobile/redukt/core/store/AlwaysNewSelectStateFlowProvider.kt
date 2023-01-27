package com.daftmobile.redukt.core.store

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

internal class AlwaysNewSelectStateFlowProvider : SelectStateFlowProvider {
    override fun <State, Selected> provide(
        state: StateFlow<State>,
        selector: Selector<State, Selected>
    ): StateFlow<Selected> = SelectStateFlow(state, selector)
}

internal class SelectStateFlow<State, Selection>(
    private val flow: StateFlow<State>,
    private val selector: Selector<State, Selection>
) : StateFlow<Selection>, SynchronizedObject() {

    private var lastProcessedState: State? by atomic(null)

    private var lastMappedValue: Selection? by atomic(null)

    override val replayCache: List<Selection> get() = listOf(value)

    override val value: Selection get() = (invalidate() ?: lastMappedValue)!!

    override suspend fun collect(collector: FlowCollector<Selection>): Nothing {
        lastMappedValue?.let { collector.emit(it) }
        flow.collect { value ->
            val previousValue = lastMappedValue
            invalidate(value)
                ?.takeIf { previousValue == null || !selector.isSelectionEqual(previousValue, it) }
                ?.let { collector.emit(it) }
        }
    }

    private fun invalidate(currentState: State = flow.value): Selection? = synchronized(this) {
        val previousState = lastProcessedState
        lastProcessedState = currentState
        return when {
            previousState == null || !selector.isStateEqual(previousState, currentState) -> {
                selector.select(currentState).also { lastMappedValue = it }
            }
            else -> null
        }
    }
}