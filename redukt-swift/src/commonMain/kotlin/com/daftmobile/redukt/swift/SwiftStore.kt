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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.experimental.ExperimentalObjCName
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.native.ObjCName

/**
 * Simplified [Store] API to provide better interop with Objective-C/Swift.
 */
@ObjCName("ReduKtStore", exact = true)
public abstract class SwiftStore<State : Any> {

    /**
     * Returns internal store instance that is hidden form Objective-C. It enables implementing any missing delegation
     * to real store as an extension to this class.
     */
    @HiddenFromObjC
    public abstract val store: Store<State>

    public open val currentState: State get() = store.currentState

    /**
     *  Invokes [onStateChange] on each change of [currentState]. Returns a [Disposable] that should be
     *  disposed when subscription is no longer needed. Initial value is delivered with [onStateChange] before
     *  this method returns.
     */
    public open fun subscribe(onStateChange: (State) -> Unit): Disposable = store.state.subscribe(onStateChange)

    /**
     *  Invokes [onStateChange] on each change of selected state with [selector]. Returns a [Disposable] that should be
     *  disposed when subscription is no longer needed. Initial value is delivered with [onStateChange] before
     *  this method returns.
     */
    public open fun <Selected> subscribe(
        selector: Selector<State, Selected>,
        onStateChange: (Selected) -> Unit,
    ): Disposable = store.select(selector).subscribe(onStateChange)

    public open fun dispatch(action: Action): Unit = store.dispatch(action)

    /**
     * Calls [Store.dispatchJob] and returns a [Disposable] that cancels foreground coroutine on dispose call.
     */
    public open fun dispatchJob(action: ForegroundJobAction): Disposable = Disposable(store.dispatchJob(action)::cancel)

    private fun <T> StateFlow<T>.subscribe(onStateChange: (T) -> Unit): Disposable = drop(1)
        .onEach(onStateChange)
        .launchIn(store.coroutineScope)
        .let {
            // dropping first value and calling onStateChange here
            // guarantees that initial value is delivered before subscription returns
            onStateChange(value)
            Disposable(it::cancel)
        }

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

}

/**
 * Returns [SwiftStore] that delegates to [this] store.
 */
@HiddenFromObjC
public fun <State : Any> Store<State>.toSwiftStore(): SwiftStore<State> = SwiftStoreWrapper(this)

private class SwiftStoreWrapper<State : Any>(override val store: Store<State>) : SwiftStore<State>()