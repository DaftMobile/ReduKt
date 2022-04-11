package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.scope.DispatchScope
import kotlinx.coroutines.CoroutineStart
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

sealed interface Thunk : Action {

    interface Executable<State> : Thunk {
        fun DispatchScope<State>.execute()
    }

    abstract class Suspendable<State>(
        val coroutineContext: CoroutineContext = EmptyCoroutineContext,
        val coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    ) : Thunk {
        abstract suspend fun DispatchScope<State>.executeSuspendable()
    }

    open class Execute<State>(private val block: DispatchScope<State>.() -> Unit) : Executable<State> {
        override fun DispatchScope<State>.execute() = block()
    }

    open class Suspend<State>(
        private val block: suspend  DispatchScope<State>.() -> Unit,
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    ) : Suspendable<State>(coroutineContext, coroutineStart) {
        override suspend fun DispatchScope<State>.executeSuspendable() = block()
    }
}