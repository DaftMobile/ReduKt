package com.daftmobile.redukt.core.store

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

public typealias Selector<T, R> = (T) -> R

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector].
 * Selected state is calculated only if [stateIsEquivalent] returns false,
 * and it is emitted to subscribers only if [subStateIsEquivalent] returns false.
 */
public fun <State, SubState> Store<State>.select(
    stateIsEquivalent: (old: State, new: State) -> Boolean = { old, new -> old == new },
    subStateIsEquivalent: (old: SubState, new: SubState) -> Boolean = { old, new -> old == new },
    selector: Selector<State, SubState>
): StateFlow<SubState> = SelectStateFlow(this.state, stateIsEquivalent, subStateIsEquivalent, selector)

private class SelectStateFlow<State, SubState>(
    private val flow: StateFlow<State>,
    private val stateIsEquivalent: (old: State, new: State) -> Boolean = { old, new -> old == new },
    private val subStateIsEquivalent: (old: SubState, new: SubState) -> Boolean = { old, new -> old == new },
    private val selector: Selector<State, SubState>
) : StateFlow<SubState>, SynchronizedObject() {

    private var lastProcessedState: State? by atomic(null)

    private var lastMappedValue: SubState? by atomic(null)

    override val replayCache: List<SubState> get() = listOf(value)

    override val value: SubState get() = (invalidate() ?: lastMappedValue)!!

    override suspend fun collect(collector: FlowCollector<SubState>): Nothing {
        lastMappedValue?.let { collector.emit(it) }
        flow.collect { value ->
            val previousValue = lastMappedValue
            invalidate(value)
                ?.takeIf { previousValue == null || !subStateIsEquivalent(previousValue, it) }
                ?.let { collector.emit(it) }
        }
    }

    private fun invalidate(currentState: State = flow.value): SubState? = synchronized(this) {
        val previousState = lastProcessedState
        lastProcessedState = currentState
        return when {
            previousState == null || !stateIsEquivalent(previousState, currentState) -> {
                selector(currentState).also { lastMappedValue = it }
            }
            else -> null
        }
    }
}