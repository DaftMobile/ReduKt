package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.scope.DispatchScope
import kotlinx.coroutines.CoroutineStart
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun <State> thunkExec(block: DispatchScope<State>.() -> Unit) = object : Thunk.Executable<State> {
    override fun DispatchScope<State>.execute() = block()
}

fun <State> thunkSuspend(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend DispatchScope<State>.() -> Unit
) = object : Thunk.Suspendable<State>(coroutineContext, coroutineStart) {
    override suspend fun DispatchScope<State>.executeSuspendable() = block()
}