package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.test.TestActions
import com.daftmobile.redukt.test.tools.emptyQueue
import com.daftmobile.redukt.test.tools.pullEach
import com.daftmobile.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import kotlin.test.Test

class AssertNoMoreActionsTest {
    @Test
    fun shouldFailWhenAnyUnverifiedAction() = testAssertion(unverified = queueOf(TestActions.ActionA)) {
        shouldThrow<AssertionError> { assertNoMoreActions() }
    }

    @Test
    fun shouldNotFailWhenAllActionsVerified() = testAssertion(unverified = queueOf(TestActions.ActionA)) {
        unverified.pullEach {  }
        shouldNotThrowAny { assertNoMoreActions() }
    }

    @Test
    fun shouldNotFailWhenNoActions() = testAssertion(unverified = emptyQueue()) {
        shouldNotThrowAny { assertNoMoreActions() }
    }
}