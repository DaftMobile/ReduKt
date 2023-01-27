package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.test.assertions.assertSingleActionEquals
import com.daftmobile.redukt.test.middleware.testJobAction
import com.daftmobile.redukt.test.middleware.tester
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ThunkMiddlewareTest {

    private val tester = thunkMiddleware.tester(Unit)

    @Test
    fun shouldExecuteThunk() = tester.test {
        var executed = false
        testAction(Thunk<Unit> { executed = true })
        executed shouldBe true
    }

    @Test
    fun shouldExecuteCoThunk() = runTest {
        tester.test {
            var executed = false
            testJobAction(CoThunk<Unit> { executed = true })
            executed shouldBe true
        }
    }

    @Test
    fun shouldLaunchForegroundJobWithCoThunk() = runTest {
        tester.test {
            val registry = SingleForegroundJobRegistry()
            closure += registry + DispatchCoroutineScope(this@runTest)
            testAction(CoThunk<Unit> { })
            registry.consumeOrNull() shouldNotBe null
            coroutineContext.job.children shouldHaveSize 1
        }
    }

    @Test
    fun shouldAllowToDispatchFromThunk() = tester.test {
        testAction(Thunk<Unit> { dispatch(TestAction) })
        assertSingleActionEquals(TestAction)
    }

    @Test
    fun shouldAllowToDispatchFromCoThunk() = runTest {
        tester.test {
            testJobAction(CoThunk<Unit> { dispatch(TestAction) })
            assertSingleActionEquals(TestAction)
        }
    }

    private object TestAction : Action
}
