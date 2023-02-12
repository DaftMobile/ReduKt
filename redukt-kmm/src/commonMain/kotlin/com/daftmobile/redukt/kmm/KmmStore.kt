@file:OptIn(ExperimentalObjCRefinement::class, ExperimentalObjCName::class)

package com.daftmobile.redukt.kmm

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
public abstract class KmmStore<State : Any> {

    @HiddenFromObjC
    protected abstract val store: Store<State>

    public open fun subscribe(onStateChange: (State) -> Unit): Disposable = store.state.subscribe(onStateChange)

    @ShouldRefineInSwift
    public open fun <Selected> subscribe(
        selector: Selector<State, Selected>,
        onStateChange: (Selected) -> Unit
    ): Disposable = store.select(selector).subscribe(onStateChange)

    @ShouldRefineInSwift
    public open fun <Selected> subscribe(
        selector: (State) -> Selected,
        onStateChange: (Selected) -> Unit
    ): Disposable = store.select(selector).subscribe(onStateChange)

    public open fun dispatch(action: Action): Unit = store.dispatch(action)

    public open fun dispatchJob(action: ForegroundJobAction): Disposable = Disposable(store.dispatchJob(action)::cancel)

    private fun <T> Flow<T>.subscribe(onStateChange: (T) -> Unit): Disposable = onEach(onStateChange)
        .launchIn(store.coroutineScope)
        .let { Disposable(it::cancel) }
}

/**
 * [previewStore] equivalent that implements [KmmStore].
 */
@ObjCName("ReduKtPreviewStore", exact = true)
public class KmmPreviewStore<State : Any>(
    initialState: State,
    reducer: Reducer<State>,
) : KmmStore<State>() {

    public constructor(initialState: State) : this(initialState, { state, _ -> state })

    @HiddenFromObjC
    override val store: Store<State> = previewStore(initialState = initialState, reducer = reducer)
}

/**
 * Returns [KmmStore] that delegates to [this] store.
 */
@HiddenFromObjC
public fun <State : Any> Store<State>.toKmmStore(): KmmStore<State> = KmmStoreWrapper(this)

private class KmmStoreWrapper<State : Any>(override val store: Store<State>) : KmmStore<State>()