package dev.redukt.test.tools

import dev.redukt.core.closure.LocalClosureContainer
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class RunningCoroutinesClosureTest {

    @Test
    fun shouldContainLocalClosureContainer() {
        RunningCoroutinesClosure().find(LocalClosureContainer).let {
            it.shouldNotBeNull()
            it::class shouldBe LocalClosureContainer()::class
        }
    }

    @Test
    fun shouldContainOnlyOneElement() {
        RunningCoroutinesClosure().scatter().size shouldBe 1
    }
}