package com.daftmobile.redukt.core.store

import com.daftmobile.redukt.core.Reducer

@DslMarker
public annotation class StoreBuilderDsl

/**
 * Provides a DSL for building a [Store]. Example below shows how to use it.
 * To fully understand it you should be familiar with [com.daftmobile.redukt.core.closure.DispatchClosure] concept.
 *
 * ```
 * fun createAppStore(isDebugVersion: Boolean) = buildStore {
 *
 *    AppState() reducedBy ::appReducer // sets an initial state and a root reducer
 *
 *    middlewares { // adds a list of middlewares in a given order
 *       +thunkMiddleware
 *       if (isDebugVersion) +logMiddleware()
 *    }
 *
 *    closure { // accumulates given closure elements into single one
 *       +DispatchCoroutineScope(MainScope())
 *       +GlobalKoinDI
 *    }
 * }
 * ```
 *
 * It is possible to split *closure* and *middlewares* into multiple blocks like this:
 *
 *```
 * fun createAppStore(isDebugVersion: Boolean) = buildStore {
 *    AppState() reducedBy ::appReducer
 *    middlewares {
 *       +thunkMiddleware
 *    }
 *    closure {
 *       +DispatchCoroutineScope(MainScope())
 *    }
 *    middlewares {
 *       if (isDebugVersion) +logMiddleware()
 *    }
 *    closure {
 *       +GlobalKoinDI
 *    }
 * }
 * ```
 * It results in exact same [Store] instance as the previous example.
 */
public fun <State> buildStore(@BuilderInference block: StoreBuilderScope<State>.() -> Unit): Store<State> {
    val builder = StoreBuilder<State>()
    builder.block()
    return builder.build()
}

/**
 * The scope for building a [Store].
 */
public interface StoreBuilderScope<State> {

    /**
     * Provides a block to add middlewares to a [Store].
     */
    @StoreBuilderDsl
    public fun middlewares(block: MiddlewaresBuilderScope<State>.() -> Unit)

    /**
     * Provides a block to build dispatch closure for a [Store].
     */
    @StoreBuilderDsl
    public fun closure(block: ClosureBuilderScope.() -> Unit)

    /**
     * Provides an initial state and root reducer for a [Store].
     */
    @StoreBuilderDsl
    public infix fun State.reducedBy(reducer: Reducer<State>)
}

internal class StoreBuilder<State> : StoreBuilderScope<State> {

    private val middlewareBuilder = MiddlewaresBuilder<State>()
    private val closureBuilder = ClosureBuilder()
    private var state: State? = null
    private var stateReducer: Reducer<State>? = null

    override infix fun State.reducedBy(reducer: Reducer<State>) {
        state = this
        stateReducer = reducer
    }

    override fun middlewares(block: MiddlewaresBuilderScope<State>.() -> Unit) {
        middlewareBuilder.apply(block)
    }

    override fun closure(block: ClosureBuilderScope.() -> Unit) {
        closureBuilder.apply(block)
    }

    fun build() = Store(
        requireNotNull(state) { stateAndReducerMissingMsg() },
        requireNotNull(stateReducer) { stateAndReducerMissingMsg() },
        middlewareBuilder.middlewares,
        closureBuilder.closure
    )

    private fun stateAndReducerMissingMsg() = "Main state and reducer are missing! Use `AppState() reducedBy ::appReducer` to initialize it!"
}