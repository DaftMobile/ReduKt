package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.test.middleware.tester
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ThunkMiddlewareTest {
    private val tester = thunkMiddleware<Unit>().tester(Unit)

    @Test
    fun shouldExecuteThunk() = tester.runTest {
        var executed = false
        testAction(Thunk<Unit> { executed = true })
        executed shouldBe true
    }
}