package dev.redukt.thunk

import dev.redukt.core.coroutines.launchForeground
import dev.redukt.core.middleware.Middleware
import dev.redukt.core.middleware.consumingMiddleware

public val thunkMiddleware: Middleware<*> = consumingMiddleware<_, ThunkAction<*>> { thunk ->
    when (thunk) {
        is CoThunkApi -> launchForeground { thunk.run { execute() } }
        is ThunkApi -> thunk.run { execute() }
    }
}