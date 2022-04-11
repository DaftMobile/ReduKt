package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.ActionDispatcher

interface DispatchScope<State> : ActionDispatcher {

    val state: State
}