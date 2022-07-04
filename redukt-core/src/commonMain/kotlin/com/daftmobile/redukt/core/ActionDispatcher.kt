package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.context.DispatchContext

interface ActionDispatcher {

    val dispatchContext: DispatchContext

    suspend fun dispatch(action: Action)
}
