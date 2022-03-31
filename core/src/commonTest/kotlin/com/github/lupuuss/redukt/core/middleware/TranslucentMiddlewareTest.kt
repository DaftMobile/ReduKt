package com.github.lupuuss.redukt.core.middleware

import com.github.lupuuss.redukt.core.KnownAction
import com.github.lupuuss.redukt.core.TestActionConsumer
import com.github.lupuuss.redukt.core.TestDispatchScope
import com.github.lupuuss.redukt.core.UnknownAction
import com.github.lupuuss.redukt.core.middleware.Middleware.Status.Passed
import org.kodein.mock.Mock
import org.kodein.mock.UsesMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesMocks(TestDispatchScope::class, TestActionConsumer::class)
internal class TranslucentMiddlewareTest : TestsWithMocks() {

    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var scope: TestDispatchScope
    @Mock
    lateinit var consumer: TestActionConsumer

    private val middleware = translucentMiddleware<Unit> { consumer.consume(this, it) }


    @BeforeTest
    fun setup() {
        every { scope.dispatch(isAny()) } returns Unit
        every { consumer.consume(isAny(), isAny()) } returns Unit
    }

    @Test
    fun `should call its block on any action`() {
        middleware.processWith(scope, UnknownAction)
        middleware.processWith(scope, KnownAction.A)
        middleware.processWith(scope, KnownAction.B)
        verify {
            consumer.consume(isAny(), isEqual(UnknownAction))
            consumer.consume(isAny(), isEqual(KnownAction.A))
            consumer.consume(isAny(), isEqual(KnownAction.B))
        }
    }

    @Test
    fun `should pass on every action`() {
        assertEquals(Passed, middleware.processWith(scope, UnknownAction))
        assertEquals(Passed, middleware.processWith(scope, KnownAction.A))
        assertEquals(Passed, middleware.processWith(scope, KnownAction.B))
    }

}