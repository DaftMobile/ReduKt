package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.test.assertions.ActionsAssertScope
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.CoThunkApi
import com.daftmobile.redukt.thunk.ThunkApi

public fun <State> ThunkApi<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, closureProvider = { closure }).run {
        execute()
        testBlock()
    }
}

public suspend fun <State> CoThunkApi<State>.testExecute(
    state: State,
    closure: DispatchClosure = EmptyDispatchClosure,
    testBlock: suspend ActionsAssertScope.() -> Unit
) {
    SpyingDispatchScope(stateProvider = { state }, closureProvider = { closure }).run {
        execute()
        testBlock()
    }
}
