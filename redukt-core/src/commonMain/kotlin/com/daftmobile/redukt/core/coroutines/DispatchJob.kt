package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.JobAction
import com.daftmobile.redukt.core.closure.localClosure
import com.daftmobile.redukt.core.closure.withLocalClosure
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun DispatchScope<*>.launchForeground(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = localClosure[DispatchCoroutineScope]
    .launch(context, start, block)
    .also(localForegroundJobRegistry::register)

public fun Flow<*>.launchInForeground(scope: DispatchScope<*>): Job = scope.launchForeground { collect() }

public fun DispatchScope<*>.dispatchJob(action: JobAction): Job {
    return withLocalClosure(SingleForegroundJobRegistry()) {
        dispatch(action)
        localForegroundJobRegistry.consume()
    }
}

public fun DispatchScope<*>.dispatchJobIn(action: JobAction, scope: CoroutineScope): Job {
    return withLocalClosure(SingleForegroundJobRegistry() + DispatchCoroutineScope(scope)) {
        dispatch(action)
        localForegroundJobRegistry.consume()
    }
}

public suspend fun DispatchScope<*>.joinDispatchJob(action: JobAction): Unit = coroutineScope {
    dispatchJobIn(action, this)
}

private val DispatchScope<*>.localForegroundJobRegistry: ForegroundJobRegistry get() = localClosure[ForegroundJobRegistry]
