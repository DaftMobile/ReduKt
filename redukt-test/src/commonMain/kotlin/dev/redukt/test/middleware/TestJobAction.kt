package dev.redukt.test.middleware

import dev.redukt.core.coroutines.DispatchCoroutineScope
import dev.redukt.core.coroutines.ForegroundJobAction
import dev.redukt.core.coroutines.SingleForegroundJobRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope

/**
 * Calls middleware under test with given [action] and joins foreground job.
 */
public suspend fun MiddlewareTestScope<*>.testJobAction(action: ForegroundJobAction): Unit = coroutineScope {
    testJobActionIn(this, action)
}

/**
 * Calls middleware under test with given [action] and provides a [scope] for a foreground job.
 */
public fun MiddlewareTestScope<*>.testJobActionIn(scope: CoroutineScope, action: ForegroundJobAction): Job {
    val registry = SingleForegroundJobRegistry()
    val prevClosure = closure
    closure += registry + DispatchCoroutineScope(scope)
    testAction(action)
    closure = prevClosure
    return registry.consume()
}