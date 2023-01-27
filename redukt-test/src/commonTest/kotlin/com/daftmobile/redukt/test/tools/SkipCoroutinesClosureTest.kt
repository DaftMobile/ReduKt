package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.closure.LocalClosureContainer
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

internal class SkipCoroutinesClosureTest {

    @Test
    fun shouldContainTestForegroundJobRegistry() {
        SkipCoroutinesClosure()[ForegroundJobRegistry].shouldBeInstanceOf<TestForegroundJobRegistry>()
    }

    @Test
    fun shouldContainTestLocalClosureContainer() {
        SkipCoroutinesClosure()[LocalClosureContainer].shouldBeInstanceOf<TestLocalClosureContainer>()
    }

    @Test
    fun shouldContainOnly2Elements() {
        SkipCoroutinesClosure().scatter().size shouldBe 2
    }
}