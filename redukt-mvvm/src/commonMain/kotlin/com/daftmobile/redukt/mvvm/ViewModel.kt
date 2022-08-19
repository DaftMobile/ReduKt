package com.daftmobile.redukt.mvvm

import com.daftmobile.redukt.core.SuspendAction
import com.daftmobile.redukt.core.coroutines.asyncDispatchIn
import com.daftmobile.redukt.core.store.Store
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

public abstract class ViewModel<State>(
    protected val store: Store<State>,
    private val defaultStarted: SharingStarted = SharingStarted.WhileSubscribed()
) : CoreViewModel() {

    protected fun <T> select(
        started: SharingStarted = defaultStarted,
        selector: (State) -> T
    ): StateFlow<T> = store.state.mapState(started = started, selector)

    protected fun <T, R> StateFlow<T>.mapState(
        started: SharingStarted = defaultStarted,
        transform: (T) -> R
    ): StateFlow<R> = map(transform).stateInHere(started, transform(value))

    protected fun <T> Flow<T>.stateInHere(
        started: SharingStarted = defaultStarted
    ): StateFlow<T?> = stateIn(viewModelScope, started, null)

    protected fun <T> Flow<T>.stateInHere(
        started: SharingStarted = defaultStarted,
        initialValue: T
    ): StateFlow<T> = stateIn(viewModelScope, started, initialValue)

    protected fun Store<*>.asyncDispatchInHere(action: SuspendAction): Job = asyncDispatchIn(action, viewModelScope)
}