package co.redukt.core.middleware

import co.redukt.core.Action
import co.redukt.core.KnownAction
import co.redukt.core.UnknownAction
import co.redukt.test.assertions.expectActionsSequence
import co.redukt.test.assertions.expectNoMoreActions
import co.redukt.test.middleware.testAllActions
import co.redukt.test.middleware.tester
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
