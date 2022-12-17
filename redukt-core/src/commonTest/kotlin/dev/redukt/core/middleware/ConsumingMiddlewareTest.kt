package dev.redukt.core.middleware

import dev.redukt.core.Action
import dev.redukt.core.KnownAction
import dev.redukt.core.UnknownAction
import dev.redukt.test.assertions.assertNoActions
import dev.redukt.test.assertions.assertSingleActionEquals
import dev.redukt.test.middleware.tester
import kotlin.test.Test

internal class ConsumingMiddlewareTest {

    private object TestAction : Action

    private val middleware = consumingMiddleware<Unit, KnownAction> { dispatch(TestAction) }
    private val tester = middleware.tester(Unit)

    @Test
    fun shouldPassOnUnknownType() = tester.test {
        testAction(UnknownAction)
        assertNext { assertSingleActionEquals(UnknownAction) }
    }

    @Test
    fun shouldConsumeOnConsumableType() = tester.test {
        assertNext { assertNoActions() }
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
