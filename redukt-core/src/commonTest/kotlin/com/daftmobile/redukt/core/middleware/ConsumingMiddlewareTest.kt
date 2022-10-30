package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.expectNoActions
import com.daftmobile.redukt.test.assertions.expectSingleActionEquals
import com.daftmobile.redukt.test.middleware.tester
import kotlin.test.Test

internal class ConsumingMiddlewareTest {

    private object TestAction : Action

    private val middleware = consumingMiddleware<Unit, KnownAction> { dispatch(TestAction) }
    private val tester = middleware.tester(Unit)

    @Test
    fun shouldPassOnUnknownType() = tester.test {
        testAction(UnknownAction)
        assertNext { expectSingleActionEquals(UnknownAction) }
    }

    @Test
    fun shouldConsumeOnConsumableType() = tester.test {
        assertNext { expectNoActions() }
    }

    @Test
    fun shouldCallBlockWithConsumedAction() = tester.test {
        testAction(KnownAction.A)
        expectSingleActionEquals(TestAction)
    }

    @Test
    fun shouldNotCallBlockWithUnknownAction() = tester.test {
        testAction(UnknownAction)
        expectNoActions()
    }
}
