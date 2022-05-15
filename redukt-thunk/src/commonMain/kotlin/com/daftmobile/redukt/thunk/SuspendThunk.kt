package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.scope.DispatchScope
import kotlinx.coroutines.CoroutineStart
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class SuspendThunk<State, T>(
    val context: CoroutineContext = EmptyCoroutineContext,
    val start: CoroutineStart = CoroutineStart.DEFAULT
) : Thunk<T>() {

    abstract suspend fun DispatchScope<State>.execute(): T

    open class Of<State, T>(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        private val body: suspend DispatchScope<State>.() -> T,
        ) : SuspendThunk<State, T>(context, start) {

        constructor(block: suspend DispatchScope<State>.() -> T): this(body = block)
        override suspend fun DispatchScope<State>.execute(): T = body()
    }
}

internal suspend fun <State, T> SuspendThunk<State, T>.executeWith(scope: DispatchScope<State>): T = scope.run { execute() }

@Suppress("UNCHECKED_CAST")
internal fun <State> SuspendThunk<*, *>.cast() = this as SuspendThunk<State, Any?>