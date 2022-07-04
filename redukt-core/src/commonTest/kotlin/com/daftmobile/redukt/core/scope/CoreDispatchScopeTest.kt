package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.TestDispatchContext
import com.daftmobile.redukt.core.context.EmptyDispatchContext
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class CoreDispatchScopeTest {

    @Test
    fun stateGetterShouldReturnGetStateResult() {
        var state = 7312
        val scope = CoreDispatchScope(
            dispatchContext = EmptyDispatchContext,
            dispatchFunction = {},
            getState = { state }
        )
        scope.state shouldBeExactly 7312
        state = 1234
        scope.state shouldBeExactly 1234
    }

    @Test
    fun dispatchShouldCallDispatchFunction() = runTest {
        var calledAction: Action? = null
        val scope = CoreDispatchScope(
            dispatchContext = EmptyDispatchContext,
            dispatchFunction = { calledAction = it },
            getState = {}
        )
        scope.dispatch(KnownAction.A)
        calledAction shouldBe KnownAction.A
    }

    @Test
    fun dispatchContextGetterShouldReturnPassedContext() {
        val context = TestDispatchContext()
        val scope = CoreDispatchScope(
            dispatchContext = context,
            dispatchFunction = {},
            getState = {}
        )
        scope.dispatchContext shouldBe context
    }
}