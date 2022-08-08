package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.expectActionEquals
import com.daftmobile.redukt.test.assertions.expectNoActions
import com.daftmobile.redukt.test.middleware.tester
import kotlin.test.Test

internal class ConsumingMiddlewareTest {

    private object TestAction : Action

    private val middleware = consumingMiddleware<Unit, KnownAction> { dispatch(TestAction) }
    private val tester = middleware.tester(Unit)

    @Test
    fun shouldPassOnUnknownType() = tester.runTest {
        testAction(UnknownAction)
        assertNext { expectActionEquals(UnknownAction) }
    }

    @Test
    fun shouldConsumeOnConsumableType() = tester.runTest {
        assertNext { expectNoActions() }
    }

    @Test
    fun shouldCallBlockWithConsumedAction() = tester.runTest {
        testAction(KnownAction.A)
        expectActionEquals(TestAction)
    }

    @Test
    fun shouldNotCallBlockWithUnknownAction() = tester.runTest {
        testAction(UnknownAction)
        expectNoActions()
    }
}