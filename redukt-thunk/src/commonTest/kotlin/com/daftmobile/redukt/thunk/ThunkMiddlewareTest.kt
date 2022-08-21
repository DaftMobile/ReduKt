package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.closure.LocalClosure
import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import com.daftmobile.redukt.core.coroutines.SingleForegroundJobRegistry
import com.daftmobile.redukt.test.middleware.tester
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ThunkMiddlewareTest {

    private val tester = thunkMiddleware<Unit>().tester(Unit)

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
            awaitTestAction(CoThunk<Unit> { executed = true })
            executed shouldBe true
        }
    }

    @Test
    fun shouldLaunchForegroundJobWithCoThunk() = runTest {
        tester.test {
            val registry = SingleForegroundJobRegistry()
            val slot = closure[LocalClosure].registerNewSlot(registry + DispatchCoroutineScope(this@runTest))
            testAction(CoThunk<Unit> { })
            closure[LocalClosure].removeSlot(slot)
            registry.consumeOrNull() shouldNotBe null
        }
    }
}
