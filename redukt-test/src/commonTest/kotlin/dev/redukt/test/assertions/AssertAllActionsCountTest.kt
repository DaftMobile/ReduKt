package dev.redukt.test.assertions

import dev.redukt.test.TestActions.ActionA
import dev.redukt.test.TestActions.ActionB
import dev.redukt.test.TestActions.ActionC
import dev.redukt.test.tools.pullEach
import dev.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldThrow
import kotlin.test.Test

internal class AssertAllActionsCountTest {
    private val actions = queueOf(ActionA, ActionB, ActionC)

    @Test
    fun shouldNotFailWhenActionCountMatches() = testAssertion(unverified = actions) {
        assertAllActionsCount(3)
    }

    @Test
    fun shouldNotFailWhenActionCountMatchesEvenIfVerified() = testAssertion(unverified = actions) {
        unverified.pullEach {  }
        assertAllActionsCount(3)
    }

    @Test
    fun shouldFailWhenActionsCountDoesNotMatch() =  testAssertion(unverified = actions) {
        shouldThrow<AssertionError> {
            assertAllActionsCount(2)
        }
    }
}