package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.TestDispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CoreDispatchScopeTest {

    @Test
    fun stateGetterShouldReturnGetStateResult() {
        var state = 7312
        val scope = CoreDispatchScope(
            dispatchContext = EmptyDispatchContext,
            dispatchFunction = {},
            getState = { state }
        )
        assertEquals(7312, scope.state)
        state = 1234
        assertEquals(1234, scope.state)
    }

    @Test
    fun dispatchShouldCallDispatchFunction() {
        var calledWithAction: Action? = null
        val scope = CoreDispatchScope(
            dispatchContext = EmptyDispatchContext,
            dispatchFunction = { calledWithAction = it },
            getState = {}
        )
        scope.dispatch(KnownAction.A)
        assertEquals(KnownAction.A, calledWithAction)
    }

    @Test
    fun dispatchContextGetterShouldReturnPassedContext() {
        val context = TestDispatchContext()
        val scope = CoreDispatchScope(
            dispatchContext = context,
            dispatchFunction = {},
            getState = {}
        )
        assertEquals(context, scope.dispatchContext)
    }
}