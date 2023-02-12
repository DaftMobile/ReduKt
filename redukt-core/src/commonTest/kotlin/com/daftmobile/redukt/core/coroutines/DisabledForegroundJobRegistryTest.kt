package com.daftmobile.redukt.core.coroutines

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Job
import kotlin.test.Test

internal class DisabledForegroundJobRegistryTest {

    private val jobRegistry = DisabledForegroundJobRegistry

    @Test
    fun shouldReturnCompletedJobOnConsumeEvenIfNotRegistered() {
        jobRegistry.consume().isCompleted.shouldBeTrue()
    }

    @Test
    fun shouldReturnCompletedJobOnConsumeOrNullEvenIfNotRegistered() {
        jobRegistry.consumeOrNull().isCompleted.shouldBeTrue()
    }

    @Test
    fun shouldNotRegisterAnyJob() {
        val job = Job()
        jobRegistry.register(job)
        jobRegistry.consume() shouldNotBe job
    }

}