package dev.redukt.core.middleware

import dev.redukt.core.Action
import dev.redukt.core.KnownAction
import dev.redukt.core.UnknownAction
import dev.redukt.test.assertions.assertActionSequence
import dev.redukt.test.assertions.skipActions
import dev.redukt.test.middleware.testAllActions
import dev.redukt.test.middleware.tester
import kotlin.test.Test

internal class TranslucentMiddlewareTest {

    private object TestAction : Action

    private val middleware = translucentMiddleware<Unit> { dispatch(TestAction) }
    private val tester = middleware.tester(Unit)

    @Test
    fun shouldCallPassedBlockOnAnyAction() = tester.test {
        testAllActions(KnownAction.A, UnknownAction, KnownAction.B)
        assertActionSequence(TestAction, TestAction, TestAction)
    }

    @Test
    fun shouldNextOnAnyAction() = tester.test {
        testAllActions(KnownAction.A, UnknownAction, KnownAction.B)
        skipActions(n = 3)
        assertNext {
            assertActionSequence(KnownAction.A, UnknownAction, KnownAction.B)
        }
    }

}
