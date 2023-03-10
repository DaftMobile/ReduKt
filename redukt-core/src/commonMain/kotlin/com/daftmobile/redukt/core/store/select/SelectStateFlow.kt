package com.daftmobile.redukt.core.store.select

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Suppress("UNCHECKED_CAST")
internal class SelectStateFlow<State, Selection>(
    private val flow: StateFlow<State>,
    private val selector: Selector<State, Selection>
) : StateFlow<Selection>, SynchronizedObject() {

    private var lastState: Any? by atomic(NULL)
    private var lastSelection: Any? by atomic(NULL)
    private val _subscribers = MutableStateFlow(0)

    val subscribers: StateFlow<Int> get() = _subscribers

    override val replayCache: List<Selection> get() = listOf(value)

    override val value: Selection get() = getSelection()

    override suspend fun collect(collector: FlowCollector<Selection>): Nothing {
        _subscribers.update { it + 1 }
        try {
            var previousValue = getSelection()
            collector.emit(previousValue)
            flow.collect { value ->
                val selection = getSelection(value)
                if (!selector.isSelectionEqual(previousValue, selection)) {
                    previousValue = selection
                    collector.emit(selection)
                }
            }
        } finally {
            _subscribers.update { it - 1 }
        }
    }

    private fun getSelection(currentState: State = flow.value): Selection = synchronized(this) {
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
