package dev.redukt.core.middleware

import dev.redukt.core.Action
import dev.redukt.core.KnownAction
import dev.redukt.core.UnknownAction
import dev.redukt.test.assertions.expectNoActions
import dev.redukt.test.assertions.expectSingleActionEquals
import dev.redukt.test.middleware.tester
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
