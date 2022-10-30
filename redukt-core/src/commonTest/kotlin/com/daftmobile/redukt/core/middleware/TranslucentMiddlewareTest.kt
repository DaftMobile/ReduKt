package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.expectActionsSequence
import com.daftmobile.redukt.test.assertions.expectNoMoreActions
import com.daftmobile.redukt.test.middleware.testAllActions
import com.daftmobile.redukt.test.middleware.tester
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
