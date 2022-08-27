package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.JobAction
import com.daftmobile.redukt.core.closure.dispatchWithLocalClosure
import com.daftmobile.redukt.core.closure.localClosure
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun DispatchScope<*>.launchForeground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = localClosure[DispatchCoroutineScope]
    .launch(context, start, block)
    .also { localClosure[ForegroundJobRegistry].register(it) }

public fun DispatchScope<*>.dispatchJob(action: JobAction): Job {
    val registry = SingleForegroundJobRegistry()
    dispatchWithLocalClosure(registry, action)
    return registry.consume()
}

public fun DispatchScope<*>.dispatchJobIn(action: JobAction, scope: CoroutineScope): Job {
    val registry = SingleForegroundJobRegistry()
    dispatchWithLocalClosure(registry + DispatchCoroutineScope(scope), action)
    return registry.consume()
}

public suspend inline fun DispatchScope<*>.awaitDispatchJob(action: JobAction): Unit = coroutineScope {
    dispatchJobIn(action, this)
}
