package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchFunction
import com.daftmobile.redukt.core.middleware.MiddlewareScope
import com.daftmobile.redukt.core.middleware.consumingDispatch
import com.daftmobile.redukt.core.middleware.translucentDispatch
import com.daftmobile.redukt.core.middleware.translucentDispatchOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

public fun MiddlewareScope<*>.translucentDispatchFlow(block: (Flow<Action>) -> Flow<Any>): DispatchFunction {
    val sharedFlow = MutableSharedFlow<Action>()
    sharedFlow.let(block).launchIn(coroutineScope)
    return translucentDispatch { sharedFlow.emit(it, coroutineScope) }
}

public inline fun <reified T : Action> MiddlewareScope<*>.consumingDispatchFlow(
    crossinline block: (Flow<T>) -> Flow<Any>
): DispatchFunction {
    val sharedFlow = MutableSharedFlow<T>()
    sharedFlow.let(block).launchIn(coroutineScope)
    return consumingDispatch<T> { sharedFlow.emit(it, coroutineScope) }
}

public inline fun <reified T : Action> MiddlewareScope<*>.translucentDispatchFlowOf(
    crossinline block: (Flow<T>) -> Flow<Any>
): DispatchFunction {
    val sharedFlow = MutableSharedFlow<T>()
    sharedFlow.let(block).launchIn(coroutineScope)
    return translucentDispatchOf<T> { sharedFlow.emit(it, coroutineScope) }
}

@PublishedApi
internal fun <T> MutableSharedFlow<T>.emit(value: T, scope: CoroutineScope): Job = scope.launch { this@emit.emit(value) }