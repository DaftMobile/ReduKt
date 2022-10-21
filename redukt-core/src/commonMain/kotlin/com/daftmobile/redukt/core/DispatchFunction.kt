package com.daftmobile.redukt.core

/**
 * Marker interface for every [Redux action](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#actions).
 */
public interface Action

/**
 * Marker interface for every [Action] that must have a single foreground coroutine ([kotlinx.coroutines.Job]) associated with it.
 */
public interface JobAction : Action

/**
 * Type alias for [Redux dispatch function](https://redux.js.org/tutorials/fundamentals/part-2-concepts-data-flow#dispatch).
 */
public typealias DispatchFunction = (action: Action) -> Unit
