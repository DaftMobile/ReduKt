package com.daftmobile.redukt.core.store.builder

import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.core.store.StoreImpl

@DslMarker
annotation class StoreBuilderDsl

fun <State> buildStore(@BuilderInference block: StoreBuilderScope<State>.() -> Unit): Store<State> {
    val builder = StoreBuilder<State>()
    builder.block()
    return builder.build()
}

interface StoreBuilderScope<State> {

    @StoreBuilderDsl
    fun middlewares(block: MiddlewaresBuilderScope<State>.() -> Unit)

    @StoreBuilderDsl
    fun context(block: ContextBuilderScope.() -> Unit)

    @StoreBuilderDsl
    infix fun State.reducedBy(reducer: Reducer<State>)
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

    private fun stateAndReducerMissingMsg() = "Main state and reducer are missing!"
}