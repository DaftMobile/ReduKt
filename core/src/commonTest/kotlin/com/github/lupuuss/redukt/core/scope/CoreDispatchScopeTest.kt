package com.github.lupuuss.redukt.core.scope

import com.github.lupuuss.redukt.core.Action
import com.github.lupuuss.redukt.core.KnownAction
import com.github.lupuuss.redukt.core.TestDispatchContext
import com.github.lupuuss.redukt.core.context.EmptyDispatchContext
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CoreDispatchScopeTest {

    @Test
    fun `should call getState on state getter`() {
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
    fun `should call dispatchFunction on dispatch`() {
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
    fun `should return passed dispatchContext on dispatchContext getter`() {
        val context = TestDispatchContext()
        val scope = CoreDispatchScope(
            dispatchContext = context,
            dispatchFunction = {},
            getState = {}
        )
        assertEquals(context, scope.dispatchContext)
    }
}