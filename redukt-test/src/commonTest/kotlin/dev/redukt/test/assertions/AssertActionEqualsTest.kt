package dev.redukt.test.assertions

import dev.redukt.test.TestActions.ActionA
import dev.redukt.test.TestActions.ActionB
import dev.redukt.test.TestActions.ActionC
import dev.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

internal class AssertActionEqualsTest {

    private val actions = queueOf(ActionA, ActionB, ActionC)

    @Test
    fun shouldPullFirstActionFromTheQueue() = testAssertion(unverified = actions) {
        assertActionEquals(ActionA)
        unverified shouldContainExactly listOf(ActionB, ActionC)
    }

    @Test
    fun shouldThrowAssertionWhenFirstActionDoesNotEqual() = testAssertion(unverified = actions) {
        shouldThrow<AssertionError> {
            assertActionEquals(ActionB)
        }
    }
}

