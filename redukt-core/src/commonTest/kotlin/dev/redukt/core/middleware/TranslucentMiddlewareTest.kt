package dev.redukt.core.middleware

import dev.redukt.core.Action
import dev.redukt.core.KnownAction
import dev.redukt.core.UnknownAction
import dev.redukt.test.assertions.expectActionsSequence
import dev.redukt.test.assertions.expectNoMoreActions
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
        expectActionsSequence(TestAction, TestAction, TestAction)
        expectNoMoreActions()
    }

    @Test
    fun shouldNextOnAnyAction() = tester.test {
        testAllActions(KnownAction.A, UnknownAction, KnownAction.B)
        assertNext {
            expectActionsSequence(KnownAction.A, UnknownAction, KnownAction.B)
        }
    }

}
