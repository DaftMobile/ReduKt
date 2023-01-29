package com.daftmobile.redukt.core.store

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

@Suppress("UNCHECKED_CAST")
internal class SelectStateFlow<State, Selection>(
    private val flow: StateFlow<State>,
    private val selector: Selector<State, Selection>
) : StateFlow<Selection>, SynchronizedObject() {

    private var lastState: Any? by atomic(NULL)
    private var lastSelection: Any? by atomic(NULL)

    override val replayCache: List<Selection> get() = listOf(value)

    override val value: Selection get() = getOrInvalidateSelection()

    override suspend fun collect(collector: FlowCollector<Selection>): Nothing {
        val lastSelection = this.lastSelection
        if (lastSelection !== NULL) collector.emit(lastSelection as Selection)
        var prevEmittedValue: Any? = lastSelection
        flow.collect { value ->
            val prev = prevEmittedValue
            val selection = getOrInvalidateSelection(value)
            if (prev === NULL || !selector.isSelectionEqual(prev as Selection, selection)) {
                prevEmittedValue = selection
                collector.emit(selection)
            }
        }
    }

    private fun getOrInvalidateSelection(currentState: State = flow.value): Selection = synchronized(this) {
        val prevState = lastState
        lastState = currentState
        return when {
            prevState === NULL || !selector.isStateEqual(prevState as State, currentState) -> {
                selector.select(currentState).also { lastSelection = it }
            }

            else -> lastSelection as Selection
        }
    }

    // we must not refer to real null because State and Selection might be nullable
    private object NULL
}