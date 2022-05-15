
package com.daftmobile.redukt.test.thunk

import com.daftmobile.redukt.core.context.DispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import com.daftmobile.redukt.test.tools.SpyingDispatchScope
import com.daftmobile.redukt.thunk.ExecuteThunk
import com.daftmobile.redukt.thunk.SuspendThunk
import kotlinx.coroutines.test.runTest

fun <State, T> SuspendThunk<State, T>.test(
    state: State,
    context: DispatchContext = EmptyDispatchContext,
    block: ThunkTesterScope<T>.() -> Unit
) {
    runTest {
        val spyingDispatchScope = SpyingDispatchScope(
            stateProvider = { state },
            contextProvider = { context }
        )
        val result = this@test.runCatching { spyingDispatchScope.execute() }
        DefaultThunkTesterScope(result, spyingDispatchScope).apply {
            block()
            handleUnprocessedResult()
        }
    }
}

fun <State, T> ExecuteThunk<State, T>.test(
    state: State,
    context: DispatchContext = EmptyDispatchContext,
    block: ThunkTesterScope<T>.() -> Unit
) {
    val spyingDispatchScope = SpyingDispatchScope(
        stateProvider = { state },
        contextProvider = { context }
    )
    val result = this@test.runCatching { spyingDispatchScope.execute() }
    DefaultThunkTesterScope(result, spyingDispatchScope).apply {
        block()
        handleUnprocessedResult()
    }
}
