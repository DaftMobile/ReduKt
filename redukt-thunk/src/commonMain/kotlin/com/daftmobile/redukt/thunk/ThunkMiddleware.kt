package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.coroutines.launchForeground
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingMiddleware

/**
 * Consumes every thunk and executes it. If action is a [CoThunk], it is executed in a foreground coroutine.
 */
public val thunkMiddleware: Middleware<*> = consumingMiddleware<_, ThunkMarker<*>> { thunk ->
    when (thunk) {
        is CoThunk -> launchForeground { thunk.executeWith(this@consumingMiddleware) }
        is Thunk -> thunk.executeWith(this)
    }
}

@Suppress("UNCHECKED_CAST")
private fun Thunk<*>.executeWith(scope: DispatchScope<Any?>) = (this as Thunk<Any?>).run { scope.execute() }

@Suppress("UNCHECKED_CAST")
private suspend fun CoThunk<*>.executeWith(scope: DispatchScope<Any?>) = (this as CoThunk<Any?>).run { scope.execute() }
