package dev.redukt.thunk

import dev.redukt.core.DispatchScope
import dev.redukt.core.coroutines.launchForeground
import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.consumingMiddleware

/**
 * Consumes every thunk and executes it. If action is a [CoThunkAction], it is executed in a foreground coroutine.
 */
public val thunkMiddleware: Middleware<*> = consumingMiddleware<_, ThunkMarker<*>> { thunk ->
    when (thunk) {
        is CoThunkAction -> launchForeground { thunk.executeWith(this@consumingMiddleware) }
        is ThunkAction -> thunk.executeWith(this)
    }
}

@Suppress("UNCHECKED_CAST")
private fun ThunkAction<*>.executeWith(scope: DispatchScope<Any?>) = (this as ThunkAction<Any?>).run { scope.execute() }

@Suppress("UNCHECKED_CAST")
private suspend fun CoThunkAction<*>.executeWith(scope: DispatchScope<Any?>) = (this as CoThunkAction<Any?>).run { scope.execute() }