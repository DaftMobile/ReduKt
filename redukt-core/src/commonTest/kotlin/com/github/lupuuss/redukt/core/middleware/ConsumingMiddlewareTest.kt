package com.github.lupuuss.redukt.core.middleware

import com.github.lupuuss.redukt.core.*
import com.github.lupuuss.redukt.core.KnownAction
import com.github.lupuuss.redukt.core.TestDispatchScope
import com.github.lupuuss.redukt.core.UnknownAction
import org.kodein.mock.Mock
import org.kodein.mock.UsesMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesMocks(TestDispatchScope::class, TestActionConsumer::class)
internal class ConsumingMiddlewareTest : TestsWithMocks() {

    override fun setUpMocks() = injectMocks(mocker)

    @Mock lateinit var scope: TestDispatchScope
    @Mock lateinit var consumer: TestActionConsumer

    private val middleware = consumingMiddleware<Unit, KnownAction> { consumer.consume(this, it) }

    @BeforeTest
    fun setup() {
        every { scope.dispatch(isAny()) } returns Unit
        every { consumer.consume(isAny(), isAny()) } returns Unit
    }

    @Test
    fun shouldPassOnUnknownType() {
        val actual = middleware.processWith(scope, UnknownAction)
        assertEquals(actual, Middleware.Status.Passed)
    }

    @Test
    fun shouldConsumeOnConsumableType() {
        val actual = middleware.processWith(scope, KnownAction.A)
        assertEquals(actual, Middleware.Status.Consumed)
    }

    @Test
    fun shouldCallBlockWithConsumedActionAndProperScope() {
        middleware.processWith(scope, KnownAction.B)
        verify { consumer.consume(isAny(), isEqual(KnownAction.B)) }
    }

    @Test
    fun shouldNotCallBlockWithUnknownAction() {
        middleware.processWith(scope, UnknownAction)
        verify { }
    }
}