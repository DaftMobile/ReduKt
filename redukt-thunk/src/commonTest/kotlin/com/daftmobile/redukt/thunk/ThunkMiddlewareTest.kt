package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.test.middleware.tester
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ThunkMiddlewareTest {
    private val tester = thunkMiddleware<Unit>().tester(Unit)

    @Test
    fun shouldExecuteThunk() = tester.suspendTest {
        var executed = false
        awaitTestAction(Thunk<Unit> { executed = true })
        executed shouldBe true
    }
}