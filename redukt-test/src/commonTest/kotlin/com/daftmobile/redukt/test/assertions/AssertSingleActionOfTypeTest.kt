package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionD
import com.daftmobile.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import kotlin.test.Test
import kotlin.test.fail

internal class AssertSingleActionOfTypeTest {

    @Test
    fun shouldPullFirstAndOnlyActionFromTheQueue() = testAssertion(unverified = queueOf(ActionD())) {
        assertSingleActionOfType<ActionD>()
        unverified.shouldBeEmpty()
    }

    @Test
    fun shouldThrowAssertionWhenFirstActionIsNotOfGivenType() = testAssertion(unverified = queueOf(ActionA)) {
        shouldThrow<AssertionError> {
            assertSingleActionOfType<ActionD>()
        }
    }

    @Test
    fun shouldThrowAssertionWhenFirstIsOfGivenTypeButFailsAssertions() = testAssertion(unverified = queueOf(ActionD())) {
        shouldThrow<AssertionError> {
            assertSingleActionOfType<ActionD> { fail() }
        }
    }

    @Test
    fun shouldThrowAssertionWhenMoreThanOneAction() = testAssertion(unverified = queueOf(ActionD(), ActionD())) {
        shouldThrow<AssertionError> {
            assertSingleActionOfType<ActionD> { fail() }
        }
    }
}