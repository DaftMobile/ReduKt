package co.redukt.thunk

import co.redukt.core.coroutines.launchForeground
import co.redukt.core.middleware.Middleware
import co.redukt.core.middleware.consumingMiddleware

public val thunkMiddleware: Middleware<*> = consumingMiddleware<_, ThunkAction<*>> { thunk ->
    when (thunk) {
        is CoThunkApi -> launchForeground { thunk.run { execute() } }
        is ThunkApi -> thunk.run { execute() }
    }
}