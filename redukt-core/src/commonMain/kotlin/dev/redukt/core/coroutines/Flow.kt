package dev.redukt.core.coroutines

import dev.redukt.core.Action
import dev.redukt.core.DispatchFunction
import dev.redukt.core.middleware.MiddlewareScope
import dev.redukt.core.middleware.consumingDispatch
import dev.redukt.core.middleware.translucentDispatch
import dev.redukt.core.middleware.translucentDispatchOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

public fun MiddlewareScope<*>.translucentDispatchFlow(block: (Flow<Action>) -> Flow<Any>): DispatchFunction {
    val sharedFlow = MutableSharedFlow<Action>()
    val scope = coroutineScope
    sharedFlow.let(block).launchIn(scope)
    return translucentDispatch { sharedFlow.emit(it, scope) }
}

public inline fun <reified T : Action> MiddlewareScope<*>.consumingDispatchFlow(
    crossinline block: (Flow<T>) -> Flow<Any>
): DispatchFunction {
    val sharedFlow = MutableSharedFlow<T>()
    val scope = coroutineScope
    sharedFlow.let(block).launchIn(scope)
    return consumingDispatch<T> { sharedFlow.emit(it, scope) }
}

public inline fun <reified T : Action> MiddlewareScope<*>.translucentDispatchFlowOf(
    crossinline block: (Flow<T>) -> Flow<Any>
): DispatchFunction {
    val sharedFlow = MutableSharedFlow<T>()
    val scope = coroutineScope
    sharedFlow.let(block).launchIn(scope)
    return translucentDispatchOf<T> { sharedFlow.emit(it, scope) }
}

@PublishedApi
internal fun <T> MutableSharedFlow<T>.emit(value: T, scope: CoroutineScope): Job = scope.launch { this@emit.emit(value) }