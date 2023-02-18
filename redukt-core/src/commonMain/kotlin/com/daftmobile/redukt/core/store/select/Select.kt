package com.daftmobile.redukt.core.store.select

import com.daftmobile.redukt.core.store.Store
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * Type alias for state mapping function.
 */
public typealias SelectorFunction<T, R> = (T) -> R

/**
 * Associates [select] function with [isStateEqual] and [isSelectionEqual] to improve selection performance.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("ReduKtSelector", exact = true)
public interface Selector<State, Selected> {

    /**
     *  Determines if selected state should be recalculated.
     */
    public fun isStateEqual(old: State, new: State): Boolean = old == new


    /**
     * Determines if selected state has changed.
     */
    public fun isSelectionEqual(old: Selected, new: Selected): Boolean = old == new

    /**
     * Calculates selected state
     */
    public fun select(state: State): Selected
}

/**
 * Creates a [Selector] that associates [selector] function with [stateEquality] and [selectionEquality].
 */
public fun <State, Selected> createSelector(
    stateEquality: SelectorEquality<State> = SelectorEquality.Default,
    selectionEquality: SelectorEquality<Selected> = SelectorEquality.Default,
    selector: SelectorFunction<State, Selected>,
): Selector<State, Selected> = DynamicSelector(stateEquality, selectionEquality, selector)

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector] function. [selector] function is not called before
 * selected state is actually accessed using [StateFlow.value] or collected. Also, it's not called if state
 * is not changed. State is compared using [equals] so selection might be recalculated even if unrelated part of the
 * state changed. However, if selection stays the same (again compared using [equals]), it's not emitted (just like
 * every [StateFlow]). To optimize state selection, use `select` with [Selector] param.
 */
public fun <State, Selection> Store<State>.select(
    selector: SelectorFunction<State, Selection>
): StateFlow<Selection> = select(SimpleSelector(selector = selector))

/**
 * Maps [Store.state] to a new [StateFlow] using given [selector].
 * Selector function is called only if [Selector.isStateEqual] returns false (by default it refers to [equals]).
 * Also, selector function is not called before selected state is actually accessed using [StateFlow.value]
 * or collected. Selector result is emitted only if [Selector.isSelectionEqual] returns false
 * (by default it refers to [equals]).
 */
public fun <State, Selected> Store<State>.select(
    selector: Selector<State, Selected>
): StateFlow<Selected> = closure[SelectStateFlowProvider].provide(state, selector)

private data class DynamicSelector<State, Selected>(
    val stateEquality: SelectorEquality<State> = SelectorEquality.Default,
    val selectionEquality: SelectorEquality<Selected> = SelectorEquality.Default,
    val selector: SelectorFunction<State, Selected>,
): Selector<State, Selected> {
    override fun isStateEqual(old: State, new: State): Boolean = stateEquality.isEqual(old, new)

    override fun isSelectionEqual(old: Selected, new: Selected): Boolean = selectionEquality.isEqual(old, new)

    override fun select(state: State): Selected = selector(state)

}

private data class SimpleSelector<State, Selected>(
    val selector: SelectorFunction<State, Selected>
) : Selector<State, Selected> {
    override fun select(state: State): Selected = selector(state)
}
