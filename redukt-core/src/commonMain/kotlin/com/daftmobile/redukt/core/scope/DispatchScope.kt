package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.ActionDispatcher

public interface DispatchScope<State> : ActionDispatcher {

    public val state: State
}