package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import kotlin.test.Test

internal class AssertSingleActionEqualsTest {

    @Test
    fun shouldPullFirstAndOnlyActionFromTheQueue() = testAssertion(unverified = queueOf(ActionA)) {
        assertSingleActionEquals(ActionA)
        unverified.shouldBeEmpty()
    }

    @Test
    fun shouldThrowAssertionWhenFirstActionDoesNotMatch() = testAssertion(unverified = queueOf(ActionA)) {
        shouldThrow<AssertionError> {
            assertSingleActionEquals(ActionB)
        }
    }

    @Test
    fun shouldThrowAssertionWhenMoreThanOneAction() = testAssertion(unverified = queueOf(ActionA, ActionB)) {
        shouldThrow<AssertionError> {
            assertSingleActionEquals(ActionA)
        }
    }
}