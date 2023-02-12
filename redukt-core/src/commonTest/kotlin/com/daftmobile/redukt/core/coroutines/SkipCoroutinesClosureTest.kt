package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.closure.LocalClosureContainer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

internal class SkipCoroutinesClosureTest {

    @Test
    fun shouldContainTestForegroundJobRegistry() {
        skipCoroutinesClosure()[ForegroundJobRegistry].shouldBeInstanceOf<DisabledForegroundJobRegistry>()
    }

    @Test
    fun shouldContainTestLocalClosureContainer() {
        skipCoroutinesClosure()[LocalClosureContainer].shouldBeInstanceOf<DisabledLocalClosureContainer>()
    }

    @Test
    fun shouldContainOnly2Elements() {
        skipCoroutinesClosure().scatter().size shouldBe 2
    }
}