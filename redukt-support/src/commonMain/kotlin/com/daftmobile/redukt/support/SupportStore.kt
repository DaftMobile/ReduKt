package com.daftmobile.redukt.support

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.coroutines.ForegroundJobAction
import com.daftmobile.redukt.core.coroutines.coroutineScope
import com.daftmobile.redukt.core.coroutines.dispatchJob
import com.daftmobile.redukt.core.store.Store
import com.daftmobile.redukt.core.store.select
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

public open class SupportStore<State>(private val store: Store<State>) {

    public open val state: State get() = store.currentState

    public open fun subscribe(onStateChange: (State) -> Unit): Disposable = store.state.subscribe(onStateChange)
    public open fun <Selected> subscribe(
        selector: SupportSelector<State, Selected>,
        onStateChange: (Selected) -> Unit
    ): Disposable = store.select(selector).subscribe(onStateChange)

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