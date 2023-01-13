package dev.redukt.test.assertions

import dev.redukt.test.TestActions.ActionA
import dev.redukt.test.tools.emptyQueue
import dev.redukt.test.tools.pullEach
import dev.redukt.test.tools.queueOf
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import kotlin.test.Test

internal class AssertNoActionsTest {

    @Test
    fun shouldFailWhenAnyUnverifiedAction() = testAssertion(unverified = queueOf(ActionA)) {
        shouldThrow<AssertionError> { assertNoActions() }
    }

    @Test
    fun shouldFailWhenAnyAction() = testAssertion(unverified = queueOf(ActionA)) {
        unverified.pullEach {  }
        shouldThrow<AssertionError> { assertNoActions() }
    }

    @Test
    fun shouldNotFailWhenNoActions() = testAssertion(unverified = emptyQueue()) {
        shouldNotThrowAny { assertNoActions() }
    }
}