@file:OptIn(ExperimentalObjCRefinement::class, ExperimentalObjCName::class)

package com.daftmobile.redukt.swift

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.Reducer
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction
import com.daftmobile.redukt.core.coroutines.coroutineScope
import com.daftmobile.redukt.core.coroutines.dispatchJob
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.core.store.previewStore
import com.daftmobile.redukt.core.store.select.Selector
import com.daftmobile.redukt.core.store.select.select
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.experimental.ExperimentalObjCName
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.native.ObjCName
import kotlin.native.ShouldRefineInSwift

/**
 * Simplified [Store] API to provide better interop with Objective-C/Swift.
 */
@ObjCName("ReduKtStore", exact = true)
public abstract class SwiftStore<State : Any> {

    @HiddenFromObjC
    protected abstract val store: Store<State>

    public abstract val state: SwiftState<State>

    public open val currentState: State get() = state.value

    @ShouldRefineInSwift
    public open fun <Selected> select(
        selector: Selector<State, Selected>,
        onStateChange: (Selected) -> Unit
    ): OptionalSwiftState<Selected> = store.select(selector).asSwiftState(store.coroutineScope)


    @ShouldRefineInSwift
    public open fun <Selected : Any> select(selector: Selector<State, Selected>): SwiftState<Selected> = store
        .select(selector)
        .asSwiftState(store.coroutineScope)

    public open fun dispatch(action: Action): Unit = store.dispatch(action)

    public open fun dispatchJob(action: ForegroundJobAction): Disposable = Disposable(store.dispatchJob(action)::cancel)

    private fun <T> Flow<T>.subscribe(onStateChange: (T) -> Unit): Disposable = onEach(onStateChange)
        .launchIn(store.coroutineScope)
        .let { Disposable(it::cancel) }

    public companion object {

        /**
         * This declaration is not really useful, but it bounds [SwiftPreviewStore] with [SwiftStore], so it is exported to
         * iOS framework with it.
         */
        public fun <State : Any> createPreview(
            initialState: State,
            reducer: Reducer<State>,
        ): SwiftPreviewStore<State> = SwiftPreviewStore(initialState, reducer)
    }
}

/**
 * [previewStore] equivalent that implements [SwiftStore].
 */
@ObjCName("ReduKtPreviewStore", exact = true)
public class SwiftPreviewStore<State : Any>(
    initialState: State,
    reducer: Reducer<State>,
) : SwiftStore<State>() {

    public constructor(initialState: State) : this(initialState, { state, _ -> state })

    @HiddenFromObjC
    override val store: Store<State> = previewStore(initialState = initialState, reducer = reducer)


    override val state: SwiftState<State> = store.state.asSwiftState(store.coroutineScope)
}

/**
 * Returns [SwiftStore] that delegates to [this] store.
 */
@HiddenFromObjC
public fun <State : Any> Store<State>.toSwiftStore(): SwiftStore<State> = SwiftStoreWrapper(this)

private class SwiftStoreWrapper<State : Any>(override val store: Store<State>) : SwiftStore<State>() {
    override val state: SwiftState<State> = store.state.asSwiftState(store.coroutineScope)
}