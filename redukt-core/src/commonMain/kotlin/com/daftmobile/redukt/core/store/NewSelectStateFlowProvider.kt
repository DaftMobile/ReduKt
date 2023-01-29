package com.daftmobile.redukt.core.store

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

internal class NewSelectStateFlowProvider : SelectStateFlowProvider {
    override fun <State, Selected> provide(
        state: StateFlow<State>,
        selector: Selector<State, Selected>
    ): StateFlow<Selected> = SelectStateFlow(state, selector)
}

@Suppress("UNCHECKED_CAST")
internal class SelectStateFlow<State, Selection>(
    private val flow: StateFlow<State>,
    private val selector: Selector<State, Selection>
) : StateFlow<Selection>, SynchronizedObject() {

    private var lastProcessedState: Any? by atomic(NULL)
    private var lastMappedValue: Any? by atomic(NULL)

    override val replayCache: List<Selection> get() = listOf(value)

    override val value: Selection get() = invalidate()

    override suspend fun collect(collector: FlowCollector<Selection>): Nothing {
        val lastMappedValue = this.lastMappedValue
        if (lastMappedValue !== NULL) collector.emit(lastMappedValue as Selection)
        var previousValue: Any? = lastMappedValue
        flow.collect { value ->
            val prev = previousValue
            val mappedValue = invalidate(value)
            if (prev === NULL || !selector.isSelectionEqual(prev as Selection, mappedValue)) {
                previousValue = mappedValue
                collector.emit(mappedValue)
            }
        }
    }

    private fun invalidate(currentState: State = flow.value): Selection = synchronized(this) {
        val previousState = lastProcessedState
        lastProcessedState = currentState
        return when {
            previousState === NULL || !selector.isStateEqual(previousState as State, currentState) -> {
                selector.select(currentState).also { lastMappedValue = it }
            }

            else -> lastMappedValue as Selection
        }
    }

    // we must not refer to real null because State and Selection might be nullable
    private object NULL
}
