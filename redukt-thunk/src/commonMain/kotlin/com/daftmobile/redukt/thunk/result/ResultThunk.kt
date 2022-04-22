package com.daftmobile.redukt.thunk.result

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.dispatchIfPresent
import com.daftmobile.redukt.core.scope.DispatchScope
import com.daftmobile.redukt.thunk.Thunk


abstract class ResultThunk<State, T>(private val callbacks: Callbacks<T>) : Thunk.Suspendable<State>() {

    data class Callbacks<T>(
        val onSuccess: (result: T) -> Action?,
        val onError: (error: Throwable) -> Action? = { null },
        val onStart: () -> Action? = { null },
        val onFinish: () -> Action? = { null },
    )

    final override suspend fun DispatchScope<State>.executeSuspendable() {
        dispatchIfPresent(callbacks.onStart())
        runCatching { getResult() }
            .onSuccess { dispatchIfPresent(callbacks.onSuccess(it)) }
            .onFailure { dispatchIfPresent(callbacks.onError(it) ?: throw it) }
        dispatchIfPresent(callbacks.onFinish())
    }

    abstract suspend fun DispatchScope<State>.getResult(): T
}