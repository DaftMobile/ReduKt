package com.daftmobile.redukt.core.coroutines

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Job
import kotlin.test.Test

class EmptyForegroundJobRegistryTest {
    private val registry = EmptyForegroundJobRegistry

    @Test
    fun shouldNotRegisterPassedJob() {
        registry.register(Job())
        registry.consumeOrNull() shouldBe null
    }

    @Test
    fun shouldNotFailOnRegister() {
        shouldNotThrowAny {
            registry.register(Job())
        }
    }

    @Test
    fun shouldThrowIllegalStateExceptionOnConsume() {
        shouldThrow<IllegalStateException> {
            registry.register(Job())
            registry.consume()
        }
    }
}