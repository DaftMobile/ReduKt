package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions.ActionA
import com.daftmobile.redukt.test.TestActions.ActionB
import com.daftmobile.redukt.test.TestActions.ActionC
import com.daftmobile.redukt.test.tools.pullEach
import com.daftmobile.redukt.test.tools.queueOf
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