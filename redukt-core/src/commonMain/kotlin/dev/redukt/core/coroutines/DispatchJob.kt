package dev.redukt.core.coroutines

import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.core.closure.local
import dev.redukt.core.closure.localClosure
import dev.redukt.core.closure.withLocalClosure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a *foreground job* using closure form [this] scope.
 * By default, it's launched in a scope provided by [DispatchCoroutineScope]. This behaviour might be changed by [dispatchJobIn] or [joinDispatchJob].
 * Because this function uses local closure, calling it outside dispatch should not be done, because it might result in unexpected behaviour.
 *
 * @see ForegroundJobAction
 */
public fun DispatchScope<*>.launchForeground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = closure.launchForeground(context, start, block)

/**
 * Launches a foreground coroutine to collect [this] flow using given [scope].
 * @see launchForeground
 */
public fun Flow<*>.launchInForegroundOf(scope: DispatchScope<*>): Job = scope.launchForeground { collect() }

/**
 * Dispatches [action] and expects any middleware to launch a single *foreground job* logically associate with it.
 * Coroutine is launched in a scope provided by [DispatchCoroutineScope].
 * @return a job associated with [action]
 */
public fun DispatchScope<*>.dispatchJob(action: ForegroundJobAction): Job {
    return withLocalClosure(SingleForegroundJobRegistry()) {
        dispatch(action)
        localForegroundJobRegistry.consume()
    }
}

/**
 * Dispatches [action] and expects any middleware to launch a single *foreground job* logically associate with it.
 * Coroutine is launched in the [scope].
 * @return a job associated with [action]
 */
public fun DispatchScope<*>.dispatchJobIn(action: ForegroundJobAction, scope: CoroutineScope): Job {
    return withLocalClosure(SingleForegroundJobRegistry() + DispatchCoroutineScope(scope)) {
        dispatch(action)
        localForegroundJobRegistry.consume()
    }
}

/**
 *  Dispatches [action] and expects any middleware to launch a single *foreground job* logically associate with it.
 *  This function suspends until foreground job is finished.
 */
public suspend fun DispatchScope<*>.joinDispatchJob(action: ForegroundJobAction): Unit = coroutineScope {
    dispatchJobIn(action, this)
}

/**
 * Launches a *foreground job* using [this] closure.
 * By default, it's launched in a scope provided by [DispatchCoroutineScope]. This behaviour might be changed by [dispatchJobIn] or [joinDispatchJob].
 * Because this function uses local closure, calling it outside dispatch should not be done, because it might result in unexpected behaviour.
 *
 * @see ForegroundJobAction
 */
public fun DispatchClosure.launchForeground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = local[DispatchCoroutineScope]
    .launch(context, start, block)
    .also { local[ForegroundJobRegistry].register(it) }

private val DispatchScope<*>.localForegroundJobRegistry: ForegroundJobRegistry get() = localClosure[ForegroundJobRegistry]
