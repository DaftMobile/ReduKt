package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.TestActionConsumer
import com.daftmobile.redukt.core.TestDispatchScope
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.core.middleware.Middleware.Status.Consumed
import com.daftmobile.redukt.core.middleware.Middleware.Status.Passed
import io.kotest.matchers.shouldBe
import org.kodein.mock.Mock
import org.kodein.mock.UsesMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test

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
        middleware.processWith(scope, UnknownAction) shouldBe Passed
    }

    @Test
    fun shouldConsumeOnConsumableType() {
        middleware.processWith(scope, KnownAction.A) shouldBe Consumed
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