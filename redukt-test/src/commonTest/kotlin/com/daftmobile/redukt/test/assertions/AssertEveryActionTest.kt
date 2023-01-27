package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import com.daftmobile.redukt.test.tools.emptyQueue
import com.daftmobile.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import kotlin.test.Test

internal class AssertEveryActionTest {

    private val actions = queueOf(ActionA, ActionB, ActionC)

    @Test
    fun shouldPullAllUnverifiedActions() = testAssertion(unverified = actions) {
        assertEveryAction { true }
        unverified.shouldBeEmpty()
    }

    @Test
    fun shouldThrowAssertionWhenOnActionDoesNotMatchThePredicate() = testAssertion(unverified = actions) {
        shouldThrow<AssertionError> {
            assertEveryAction { it == ActionA }
        }
    }

    @Test
    fun shouldNotFailWhenNoActions() = testAssertion(unverified = emptyQueue()) {
        shouldNotThrowAny {
            assertEveryAction { false }
        }
    }
}