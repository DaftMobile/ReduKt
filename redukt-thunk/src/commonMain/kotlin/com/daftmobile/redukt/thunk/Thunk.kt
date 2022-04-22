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

    abstract class Launchable<State>(
        val context: CoroutineContext = EmptyCoroutineContext,
        val start: CoroutineStart = CoroutineStart.DEFAULT,
    ) : Thunk {
        abstract suspend fun DispatchScope<State>.launch()
    }

    open class Execute<State>(private val block: DispatchScope<State>.() -> Unit) : Executable<State> {
        override fun DispatchScope<State>.execute() = block()
    }

    open class Launch<State>(
        private val block: suspend  DispatchScope<State>.() -> Unit,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ) : Launchable<State>(context, start) {
        override suspend fun DispatchScope<State>.launch() = block()
    }
}