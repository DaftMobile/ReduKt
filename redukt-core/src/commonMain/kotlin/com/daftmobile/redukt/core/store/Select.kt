package com.daftmobile.redukt.core.store

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

/**
 * Type alias for Redux selector equivalent
 */
public typealias Selector<T, R> = (T) -> R

/**
 * Associates [selector] with [stateIsEquivalent] and [selectedIsEquivalent].
 * @param selector - calculates selected state
 * @param stateIsEquivalent - determines if selected state should be recalculated
 * @param selectedIsEquivalent - determines if selected state has changed
 */
public data class Selection<State, Selected>(
    val stateIsEquivalent: (old: State, new: State) -> Boolean = defaultEqualsEquivalent,
    val selectedIsEquivalent: (old: Selected, new: Selected) -> Boolean = defaultEqualsEquivalent,
    val selector: Selector<State, Selected>,
)

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector].
 * Selected state is calculated only if [stateIsEquivalent] returns false,
 * and it is emitted to subscribers only if [selectedIsEquivalent] returns false.
 */
public fun <State, Selected> Store<State>.select(
    stateIsEquivalent: (old: State, new: State) -> Boolean = defaultEqualsEquivalent,
    selectedIsEquivalent: (old: Selected, new: Selected) -> Boolean = defaultEqualsEquivalent,
    selector: Selector<State, Selected>
): StateFlow<Selected> = select(Selection(stateIsEquivalent, selectedIsEquivalent, selector))

/**
 * Maps [Store.state] to a new [StateFlow] using given [selection].
 */
public fun <State, Selected> Store<State>.select(
    selection: Selection<State, Selected>
): StateFlow<Selected> = SelectStateFlow(state, selection)

private val defaultEqualsEquivalent: (Any?, Any?) -> Boolean = { a, b -> a == b }

private class SelectStateFlow<State, Selected>(
    private val flow: StateFlow<State>,
    private val selection: Selection<State, Selected>
) : StateFlow<Selected>, SynchronizedObject() {

    private var lastProcessedState: State? by atomic(null)

    private var lastMappedValue: Selected? by atomic(null)

    override val replayCache: List<Selected> get() = listOf(value)

    override val value: Selected get() = (invalidate() ?: lastMappedValue)!!

    override suspend fun collect(collector: FlowCollector<Selected>): Nothing {
        lastMappedValue?.let { collector.emit(it) }
        flow.collect { value ->
            val previousValue = lastMappedValue
            invalidate(value)
                ?.takeIf { previousValue == null || !selection.selectedIsEquivalent(previousValue, it) }
                ?.let { collector.emit(it) }
        }
    }

    private fun invalidate(currentState: State = flow.value): Selected? = synchronized(this) {
        val previousState = lastProcessedState
        lastProcessedState = currentState
        return when {
            previousState == null || !selection.stateIsEquivalent(previousState, currentState) -> {
                selection.selector(currentState).also { lastMappedValue = it }
            }
            else -> null
        }
    }
}