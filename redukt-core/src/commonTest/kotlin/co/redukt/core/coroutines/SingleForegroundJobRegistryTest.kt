package co.redukt.core.coroutines

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Job
import kotlin.test.Test

class SingleForegroundJobRegistryTest {
    private val registry = SingleForegroundJobRegistry()

    @Test
    fun registerShouldNotFailOnFirstJob() {
        shouldNotThrowAny { registry.register(Job()) }
    }

    @Test
    fun registerShouldThrowIllegalStateExceptionOnRepeatedRegister() {
        registry.register(Job())
        shouldThrow<IllegalStateException> { registry.register(Job()) }
    }

    @Test
    fun consumeShouldReturnRegisteredJob() {
        val job = Job()
        registry.register(job)
        registry.consume() shouldBe job
    }

    @Test
    fun consumeShouldThrowIllegalArgumentExceptionWhenJobNotRegistered() {
        shouldThrow<IllegalArgumentException> { registry.consume() }
    }

    @Test
    fun consumeShouldThrowIllegalArgumentExceptionWhenJobAlreadyConsumed() {
        registry.register(Job())
        registry.consume()
        shouldThrow<IllegalArgumentException> { registry.consume() }
    }

    @Test
    fun consumeOrNullShouldReturnRegisteredJob() {
        val job = Job()
        registry.register(job)
        registry.consumeOrNull() shouldBe job
    }


    @Test
    fun consumeOrNullShouldReturnNullWhenJobNotRegistered() {
        registry.consumeOrNull() shouldBe null
    }

    @Test
    fun consumeOrNullShouldReturnNullWhenJobAlreadyConsumedRegistered() {
        registry.register(Job())
        registry.consumeOrNull()
        registry.consumeOrNull() shouldBe null
    }
}