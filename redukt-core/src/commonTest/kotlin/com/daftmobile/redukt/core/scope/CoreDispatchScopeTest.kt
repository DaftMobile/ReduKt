package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import io.kotest.assertions.throwables.shouldNotThrowAny
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
        scope.currentState shouldBeExactly 7312
        state = 1234
        scope.currentState shouldBeExactly 1234
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

    @Test
    fun dispatchIfPresentShouldAcceptNullWithoutFail() {
        shouldNotThrowAny {
            var dispatchedAction: Action? = null
            CoreDispatchScope(TestDispatchClosure(), { dispatchedAction = it }, { }).dispatchIfPresent(null)
            dispatchedAction shouldBe null
        }
    }

    @Test
    fun dispatchIfPresentShouldDispatchWhenActionIsNotNull() {
        var dispatchedAction: Action? = null
        CoreDispatchScope(TestDispatchClosure(), { dispatchedAction = it }, { }).dispatchIfPresent(KnownAction.A)
        dispatchedAction shouldBe KnownAction.A
    }
}