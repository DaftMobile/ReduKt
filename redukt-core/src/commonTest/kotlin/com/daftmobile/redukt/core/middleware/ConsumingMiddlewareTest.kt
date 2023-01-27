package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.assertNoActions
import com.daftmobile.redukt.test.assertions.assertSingleActionEquals
import com.daftmobile.redukt.test.middleware.tester
import kotlin.test.Test

internal class ConsumingMiddlewareTest {

    private object TestAction : Action

    private val tester = consumingMiddleware<Unit, KnownAction> { dispatch(TestAction) }.tester(Unit)

    @Test
    fun shouldPassOnUnknownType() = tester.test {
        testAction(UnknownAction)
        verifyNext { assertSingleActionEquals(UnknownAction) }
    }

    @Test
    fun shouldConsumeOnConsumableType() = tester.test {
        verifyNext { assertNoActions() }
    }

    @Test
    fun shouldCallBlockWithConsumedAction() = tester.test {
        testAction(KnownAction.A)
        assertSingleActionEquals(TestAction)
    }

    @Test
    fun shouldNotCallBlockWithUnknownAction() = tester.test {
        testAction(UnknownAction)
        assertNoActions()
    }
}
