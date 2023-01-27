package com.daftmobile.redukt.core

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class DispatchScopeTest {

    @Test
    fun dispatchIfPresentShouldAcceptNullWithoutFail() {
        shouldNotThrowAny {
            var dispatchedAction: Action? = null
            dispatchScope(dispatch = { dispatchedAction = it }, getState = { }).dispatchIfPresent(null)
            dispatchedAction shouldBe null
        }
    }

    @Test
    fun dispatchIfPresentShouldDispatchWhenActionIsNotNull() {
        var dispatchedAction: Action? = null
        dispatchScope(dispatch = { dispatchedAction = it }, getState = { }).dispatchIfPresent(KnownAction.A)
        dispatchedAction shouldBe KnownAction.A
    }
}