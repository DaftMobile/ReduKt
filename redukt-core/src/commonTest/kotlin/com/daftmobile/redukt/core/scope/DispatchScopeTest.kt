package com.daftmobile.redukt.core.scope

import com.daftmobile.redukt.core.*
import com.daftmobile.redukt.core.KnownAction
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class DispatchScopeTest {

    @Test
    fun dispatchIfPresentShouldAcceptNullWithoutFail() {
        shouldNotThrowAny {
            var dispatchedAction: Action? = null
            dispatchScope(TestDispatchClosure(), { dispatchedAction = it }, { }).dispatchIfPresent(null)
            dispatchedAction shouldBe null
        }
    }

    @Test
    fun dispatchIfPresentShouldDispatchWhenActionIsNotNull() {
        var dispatchedAction: Action? = null
        dispatchScope(TestDispatchClosure(), { dispatchedAction = it }, { }).dispatchIfPresent(KnownAction.A)
        dispatchedAction shouldBe KnownAction.A
    }
}