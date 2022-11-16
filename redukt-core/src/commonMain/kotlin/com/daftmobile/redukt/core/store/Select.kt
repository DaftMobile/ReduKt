package com.daftmobile.redukt.core.store

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

/**
 * Type alias for state mapping function.
 */
public typealias PureSelector<T, R> = (T) -> R

/**
 * Associates [selector] function with [stateEquality] and [selectionEquality].
 * @param selector - calculates selected state
 * @param stateEquality - determines if selected state should be recalculated
 * @param selectionEquality - determines if selected state has changed
 */
public data class Selector<State, Selected>(
    val stateEquality: (old: State, new: State) -> Boolean = defaultEquality,
    val selectionEquality: (old: Selected, new: Selected) -> Boolean = defaultEquality,
    val selector: PureSelector<State, Selected>,
)

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector] function. [selector] function is not called before
 * selected state is actually accessed using [StateFlow.value] or collected. Also, it's not called if state is not changed.
 * State is compared using [equals] so selection might be recalculated even if unrelated part of the state changed.
 * However, if selection stays the same (again compared using [equals]), it's not emitted (just like every [StateFlow]).
 * To optimize state selection, use `select` with [Selector] param.
 */
public fun <State, Selection> Store<State>.select(
    selector: PureSelector<State, Selection>
): StateFlow<Selection> = select(Selector(selector = selector))

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector].
 * Selector function is called only if [Selector.stateEquality] returns false (by default it refers to [equals]).
 * Also, selector function is not called before selected state is actually accessed using [StateFlow.value] or collected.
 * Selector result is emitted only if [Selector.selectionEquality] returns false (by default it refers to [equals]).
 */
public fun <State, Selected> Store<State>.select(
    selector: Selector<State, Selected>
): StateFlow<Selected> = SelectStateFlow(state, selector)

private val defaultEquality: (Any?, Any?) -> Boolean = { a, b -> a == b }

private class SelectStateFlow<State, Selection>(
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
                ?.takeIf { previousValue == null || !selector.selectionEquality(previousValue, it) }
                ?.let { collector.emit(it) }
        }
    }

    private fun invalidate(currentState: State = flow.value): Selection? = synchronized(this) {
        val previousState = lastProcessedState
        lastProcessedState = currentState
        return when {
            previousState == null || !selector.stateEquality(previousState, currentState) -> {
                selector.selector(currentState).also { lastMappedValue = it }
            }

            else -> null
        }
    }
}