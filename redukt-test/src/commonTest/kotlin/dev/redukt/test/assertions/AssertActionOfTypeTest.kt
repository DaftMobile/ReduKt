package dev.redukt.test.assertions

import dev.redukt.test.TestActions.ActionB
import dev.redukt.test.TestActions.ActionC
import dev.redukt.test.TestActions.ActionD
import dev.redukt.test.tools.queueOf
import dev.redukt.test.tools.toQueue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test
import kotlin.test.fail

internal class AssertActionOfTypeTest {

    private val actions = queueOf(ActionD(), ActionB, ActionC)

    @Test
    fun shouldPullFirstActionFromTheQueue() = testAssertion(unverified = actions) {
        assertActionOfType<ActionD>()
        unverified shouldContainExactly listOf(ActionB, ActionC)
    }

    @Test
    fun shouldThrowAssertionWhenFirstActionIsNotOfGivenType() = testAssertion(unverified = actions.drop(1).toQueue()) {
        shouldThrow<AssertionError> {
            assertActionOfType<ActionD>()
        }
    }

    @Test
    fun shouldThrowAssertionWhenFirstIsOfGivenTypeButFailsAssertions() = testAssertion(unverified = actions) {
        shouldThrow<AssertionError> {
            assertActionOfType<ActionD> { fail() }
        }
    }
}