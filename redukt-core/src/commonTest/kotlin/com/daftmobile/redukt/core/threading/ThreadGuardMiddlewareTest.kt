package com.daftmobile.redukt.core.threading

import com.daftmobile.redukt.core.KnownAction
import com.daftmobile.redukt.core.runCoroutineTest
import com.daftmobile.redukt.test.middleware.tester
import io.kotest.assertions.throwables.shouldThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.test.Ignore
import kotlin.test.Test

class ThreadGuardMiddlewareTest {

    private val tester = threadGuardMiddleware.tester(Unit)

    @Test
    @Ignore
    fun shouldProperlyGuardThreadWhenMultipleThreadsInvolved() = tester.runCoroutineTest {
        val mainThread = KtThread.current()
        testAction(KnownAction.A)
        withContext(Dispatchers.Default) {
            // This assertion should only occur if thread really changed
            // Effectively this assertion is not performed on JS platform
            if (mainThread != KtThread.current()) {
                shouldThrow<IllegalStateException> { testAction(KnownAction.A) }
            }
        }
    }

    @Test
    fun shouldNotThrowWhenThreadDidNotChange() = tester.runCoroutineTest {
        testAction(KnownAction.A)
        testAction(KnownAction.B)
    }
}