package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import com.daftmobile.redukt.test.TestActions.ActionD
import com.daftmobile.redukt.test.tools.queueOf
import com.daftmobile.redukt.test.tools.toQueue
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