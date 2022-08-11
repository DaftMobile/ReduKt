package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.CoreDispatchScope
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.TestDispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoreDispatchScopeTest {

    @Test
    fun stateGetterShouldReturnGetStateResult() {
        var state = 7312
        val scope = CoreDispatchScope(
            closure = EmptyDispatchClosure,
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
            closure = EmptyDispatchClosure,
            dispatchFunction = { calledAction = it },
            getState = {}
        )
        scope.dispatch(KnownAction.A)
        calledAction shouldBe KnownAction.A
    }

    @Test
    fun dispatchClosureGetterShouldReturnPassedClosure() {
        val closure = TestDispatchClosure()
        val scope = CoreDispatchScope(
            closure = closure,
            dispatchFunction = {},
            getState = {}
        )
        scope.closure shouldBe closure
    }
}