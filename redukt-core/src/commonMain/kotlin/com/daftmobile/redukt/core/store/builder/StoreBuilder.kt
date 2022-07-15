package com.daftmobile.redukt.core.store.builder

import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.core.store.StoreImpl

@DslMarker
public annotation class StoreBuilderDsl

public fun <State> buildStore(@BuilderInference block: StoreBuilderScope<State>.() -> Unit): Store<State> {
    val builder = StoreBuilder<State>()
    builder.block()
    return builder.build()
}

public interface StoreBuilderScope<State> {

    @StoreBuilderDsl
    public fun middlewares(block: MiddlewaresBuilderScope<State>.() -> Unit)

    @StoreBuilderDsl
    public fun context(block: ContextBuilderScope.() -> Unit)

    @StoreBuilderDsl
    public infix fun State.reducedBy(reducer: Reducer<State>)
}

internal class StoreBuilder<State> : StoreBuilderScope<State> {

    private val middlewareBuilder = MiddlewaresBuilder<State>()
    private val contextBuilder = ContextBuilder()
    private var state: State? = null
    private var stateReducer: Reducer<State>? = null

    override infix fun State.reducedBy(reducer: Reducer<State>) {
        state = this
        stateReducer = reducer
    }

    override fun middlewares(block: MiddlewaresBuilderScope<State>.() -> Unit) {
        middlewareBuilder.apply(block)
    }

    override fun context(block: ContextBuilderScope.() -> Unit) {
        contextBuilder.apply(block)
    }

    fun build() = StoreImpl(
        requireNotNull(state) { stateAndReducerMissingMsg() },
        requireNotNull(stateReducer) { stateAndReducerMissingMsg() },
        middlewareBuilder.middlewares,
        contextBuilder.context
    )

    private fun stateAndReducerMissingMsg() = "Main state and reducer are missing! Use `AppState() reducedBy ::appReducer` to initialize it!"
}