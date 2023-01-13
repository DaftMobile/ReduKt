package dev.redukt.test.assertions

import dev.redukt.test.TestActions.ActionA
import dev.redukt.test.TestActions.ActionB
import dev.redukt.test.TestActions.ActionC
import dev.redukt.test.tools.emptyQueue
import dev.redukt.test.tools.queueOf
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