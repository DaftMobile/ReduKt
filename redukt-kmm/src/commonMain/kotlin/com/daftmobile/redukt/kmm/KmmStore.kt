@file:OptIn(ExperimentalObjCRefinement::class)

package com.daftmobile.redukt.kmm

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction
import com.daftmobile.redukt.core.coroutines.coroutineScope
import com.daftmobile.redukt.core.coroutines.dispatchJob
import com.daftmobile.redukt.core.store.Store
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

@OptIn(ExperimentalObjCName::class)
@ObjCName("ReduKtStore", exact = true)
public abstract class KmmStore<State> {

    @HiddenFromObjC
    protected abstract val store: Store<State>

    public abstract val state: State

    public abstract fun subscribe(onStateChange: (State) -> Unit): Disposable

    @ShouldRefineInSwift
    public abstract fun <Selected> subscribe(
        selector: Selector<State, Selected>,
        onStateChange: (Selected) -> Unit
    ): Disposable

    @ShouldRefineInSwift
    public abstract fun <Selected> subscribe(
        selector: (State) -> Selected,
        onStateChange: (Selected) -> Unit
    ): Disposable

    public abstract fun dispatch(action: Action)

    public abstract fun dispatchJob(action: ForegroundJobAction): Disposable
}

public fun <State : Any> Store<State>.toKmmStore(): KmmStore<State> = KmmStoreImpl(this)

private class KmmStoreImpl<State : Any>(override val store: Store<State>) : KmmStore<State>() {

    override val state: State get() = store.currentState

    override fun subscribe(onStateChange: (State) -> Unit): Disposable = store.state.subscribe(onStateChange)

    override fun <Selected> subscribe(
        selector: Selector<State, Selected>,
        onStateChange: (Selected) -> Unit
    ): Disposable = store.select(selector).subscribe(onStateChange)

    override fun <Selected> subscribe(
        selector: (State) -> Selected,
        onStateChange: (Selected) -> Unit
    ): Disposable = store.select(selector).subscribe(onStateChange)

    override fun dispatch(action: Action): Unit = store.dispatch(action)

    override fun dispatchJob(action: ForegroundJobAction): Disposable = Disposable(store.dispatchJob(action)::cancel)

    private fun <T> Flow<T>.subscribe(onStateChange: (T) -> Unit): Disposable = onEach(onStateChange)
        .launchIn(store.coroutineScope)
        .let { Disposable(it::cancel) }
}