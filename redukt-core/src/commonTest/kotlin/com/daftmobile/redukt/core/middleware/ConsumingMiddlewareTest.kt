package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.test.assertions.expectActionEquals
import com.daftmobile.redukt.test.assertions.expectNoActions
import com.daftmobile.redukt.test.middleware.expectToBeConsumed
import com.daftmobile.redukt.test.middleware.expectToBePassed
import com.daftmobile.redukt.test.middleware.tester
import kotlin.test.Test

internal class ConsumingMiddlewareTest {

    private object TestAction : Action

    private val middleware = consumingMiddleware<Unit, KnownAction> { dispatch(TestAction) }
    private val tester = middleware.tester(Unit)

    @Test
    fun shouldPassOnUnknownType() = tester.test {
        onAction(UnknownAction).expectToBePassed()
    }

    @Test
    fun shouldConsumeOnConsumableType() = tester.test {
        onAction(KnownAction.A).expectToBeConsumed()
    }

    @Test
    fun shouldCallBlockWithConsumedAction() = tester.test {
        onAction(KnownAction.A)
        expectActionEquals(TestAction)
    }

    @Test
    fun shouldNotCallBlockWithUnknownAction() = tester.test {
        onAction(UnknownAction)
        expectNoActions()
    }
}