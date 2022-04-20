package com.daftmobile.redukt.core.middleware

import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.TestActionConsumer
import com.daftmobile.redukt.core.TestDispatchScope
import com.daftmobile.redukt.core.UnknownAction
import com.daftmobile.redukt.core.middleware.Middleware.Status.Passed
import io.kotest.matchers.shouldBe
import org.kodein.mock.Mock
import org.kodein.mock.UsesMocks
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.BeforeTest
import kotlin.test.Test

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
    fun shouldCallPassedBlockOnAnyAction() {
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
    fun shouldPassOnAnyAction() {
        middleware.processWith(scope, UnknownAction) shouldBe Passed
        middleware.processWith(scope, KnownAction.A) shouldBe Passed
        middleware.processWith(scope, KnownAction.B) shouldBe Passed
    }

}