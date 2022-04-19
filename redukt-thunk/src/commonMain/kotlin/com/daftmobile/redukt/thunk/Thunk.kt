package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

sealed interface Thunk : Action {

    interface Executable<State> : Thunk {
        fun DispatchScope<State>.execute()
    }

    abstract class Suspendable<State>(
        val coroutineContext: CoroutineContext = EmptyCoroutineContext,
        val alwaysDispatch: Boolean = false,
    ) : Thunk {
        abstract suspend fun DispatchScope<State>.executeSuspendable()
    }

    open class Execute<State>(private val block: DispatchScope<State>.() -> Unit) : Executable<State> {
        override fun DispatchScope<State>.execute() = block()
    }

    open class Suspend<State>(
        private val block: suspend  DispatchScope<State>.() -> Unit,
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        alwaysDispatch: Boolean = false,
    ) : Suspendable<State>(coroutineContext, alwaysDispatch) {
        override suspend fun DispatchScope<State>.executeSuspendable() = block()
    }
}