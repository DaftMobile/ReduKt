package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import com.daftmobile.redukt.test.tools.queueOf
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

