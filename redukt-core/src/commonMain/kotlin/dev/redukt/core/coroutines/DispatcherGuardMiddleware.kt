package dev.redukt.core.coroutines

import dev.redukt.core.closure.localClosure
import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.translucentDispatch
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Makes sure that [CoroutineDispatcher] is not changed by the local changed [DispatchCoroutineScope].
 */
@OptIn(ExperimentalStdlibApi::class)
public val coroutineDispatcherGuardMiddleware: Middleware<*> = {
    val initialDispatcher = coroutineScope.coroutineContext[CoroutineDispatcher]
    translucentDispatch {
        val currentDispatcher = localClosure[DispatchCoroutineScope].coroutineContext[CoroutineDispatcher]
        if (currentDispatcher != initialDispatcher) {
            error(
                "Launching coroutine with $currentDispatcher from local closure (e.g. dispatchJobIn), but original DispatchCoroutineScope contains $initialDispatcher! You should not change initial CoroutineDispatcher!"
            )
        }
    }
}
