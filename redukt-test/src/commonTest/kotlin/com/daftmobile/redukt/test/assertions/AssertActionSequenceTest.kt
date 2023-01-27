package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import com.daftmobile.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldThrow
import kotlin.test.Test

internal class AssertActionSequenceTest {
    private val actions = queueOf(ActionA, ActionB)

    @Test
    fun shouldNotFailWhenActionsSequenceEquals() = testAssertion(unverified = actions) {
        assertActionSequence(ActionA, ActionB)
    }

    @Test
    fun shouldWhenActionsSequenceDoesNotMatch() = testAssertion(unverified = queueOf(ActionB, ActionC)) {
        shouldThrow<AssertionError> { assertActionSequence(ActionA, ActionB) }
    }

    @Test
    fun shouldFailWhenMoreActions() = testAssertion(unverified = queueOf(ActionA, ActionB, ActionC)) {
        shouldThrow<AssertionError> { assertActionSequence(ActionA, ActionB) }
    }
}