package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.closure.LocalClosureContainer
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class RunningCoroutinesClosureTest {

    @Test
    fun shouldContainLocalClosureContainer() {
        runningCoroutinesClosure().find(LocalClosureContainer).let {
            it.shouldNotBeNull()
            it::class shouldBe LocalClosureContainer()::class
        }
    }

    @Test
    fun shouldContainEmptyForegroundJobRegistry() {
        runningCoroutinesClosure().find(ForegroundJobRegistry).shouldBe(EmptyForegroundJobRegistry)
    }

}