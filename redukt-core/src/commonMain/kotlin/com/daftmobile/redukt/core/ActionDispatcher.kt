package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.closure.DispatchClosure

public interface ActionDispatcher {

    public val closure: DispatchClosure

    public suspend fun dispatch(action: Action)
}

public suspend fun ActionDispatcher.dispatchIfPresent(action: Action?) {
    action?.let { dispatch(it) }
}
