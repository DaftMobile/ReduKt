package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.assertActionSequence
import com.daftmobile.redukt.test.assertions.skipActions
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
        assertActionSequence(TestAction, TestAction, TestAction)
    }

    @Test
    fun shouldNextOnAnyAction() = tester.test {
        testAllActions(KnownAction.A, UnknownAction, KnownAction.B)
        skipActions(n = 3)
        verifyNext {
            assertActionSequence(KnownAction.A, UnknownAction, KnownAction.B)
        }
    }

}
