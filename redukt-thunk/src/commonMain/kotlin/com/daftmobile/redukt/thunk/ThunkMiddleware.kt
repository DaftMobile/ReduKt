package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.coroutines.launchForeground
import com.daftmobile.redukt.core.middleware.Middleware
import com.daftmobile.redukt.core.middleware.consumingMiddleware

public val thunkMiddleware: Middleware<*> = consumingMiddleware<_, ThunkAction<*>> { thunk ->
    when (thunk) {
        is CoThunkApi -> launchForeground { thunk.run { execute() } }
        is ThunkApi -> thunk.run { execute() }
    }
}