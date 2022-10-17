package com.daftmobile.redukt.core

/**
 * Type alias for a [pure function](https://en.wikipedia.org/wiki/Pure_function) that accepts the current state of type [T] and an action and returns a new state.
 * This is equivalent to [Redux reducer](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#reducers).
 */
public typealias Reducer<T> = (T, Action) -> T

/**
 * Combines reducers of the same type T into a single reducer. The first child reducer is invoked with the same
 * action and state as the parent reducer. Every subsequent child is invoked with state returned by its predecessor.
 * @return New reducer that combines [reducers] in a given order.
 */
public fun <T> combineReducers(vararg reducers: Reducer<T>): Reducer<T> = { initialState, action ->
    reducers.fold(initialState) { prevState, reducer -> reducer(prevState, action) }
}