package dev.redukt.core

/**
 * Marker interface for every [Redux action](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#actions).
 */
public interface Action

/**
 * Type alias for [Redux dispatch function](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#dispatch).
 * In contrast to Redux dispatch, it returns no value.
 */
public typealias DispatchFunction = (action: Action) -> Unit
