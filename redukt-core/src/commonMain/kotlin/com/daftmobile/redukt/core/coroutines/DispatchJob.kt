package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.localClosure
import com.daftmobile.redukt.core.closure.withLocalClosure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a *foreground job*. CoroutineScope for this coroutine depends on function used to dispatch it.
 * By default, it's launched in a scope provided by [DispatchCoroutineScope]. This behaviour might be changed by [dispatchJobIn] or [joinDispatchJob].
 *
 * @see ForegroundJobAction
 */
public fun DispatchScope<*>.launchForeground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = localClosure[DispatchCoroutineScope]
    .launch(context, start, block)
    .also(localForegroundJobRegistry::register)

/**
 * Launches a foreground coroutine to collect [this] flow using given [scope].
 * @see launchForeground
 */
public fun Flow<*>.launchInForeground(scope: DispatchScope<*>): Job = scope.launchForeground { collect() }

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

private val DispatchScope<*>.localForegroundJobRegistry: ForegroundJobRegistry get() = localClosure[ForegroundJobRegistry]
