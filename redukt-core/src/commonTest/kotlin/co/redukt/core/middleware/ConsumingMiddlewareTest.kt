package co.redukt.core.middleware

import co.redukt.core.Action
import co.redukt.core.KnownAction
import co.redukt.core.UnknownAction
import co.redukt.test.assertions.expectNoActions
import co.redukt.test.assertions.expectSingleActionEquals
import co.redukt.test.middleware.tester
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
