package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.scope.DispatchScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun <State> thunkExec(block: DispatchScope<State>.() -> Unit) = object : Thunk.Executable<State> {
    override fun DispatchScope<State>.execute() = block()
}

fun <State> thunkSuspend(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    alwaysDispatch: Boolean = false,
    block: suspend DispatchScope<State>.() -> Unit
) = object : Thunk.Suspendable<State>(coroutineContext, alwaysDispatch) {
    override suspend fun DispatchScope<State>.executeSuspendable() = block()
}