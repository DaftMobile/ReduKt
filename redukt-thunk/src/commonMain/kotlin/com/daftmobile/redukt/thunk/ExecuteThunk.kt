package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.scope.DispatchScope

abstract class ExecuteThunk<State, T> : Thunk<T>() {

    abstract fun DispatchScope<State>.execute(): T

    open class Of<State, T>(private val block: DispatchScope<State>.() -> T) : ExecuteThunk<State, T>() {
        override fun DispatchScope<State>.execute(): T = block()
    }
}

internal fun <State, T> ExecuteThunk<State, T>.executeWith(scope: DispatchScope<State>): T = scope.run { execute() }

@Suppress("UNCHECKED_CAST")
internal fun <State> ExecuteThunk<*, *>.cast() = this as ExecuteThunk<State, Any?>