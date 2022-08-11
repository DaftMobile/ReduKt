package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.DispatchContext

public interface ActionDispatcher {

    public val dispatchContext: DispatchContext

    public suspend fun dispatch(action: Action)
}

public suspend fun ActionDispatcher.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}
