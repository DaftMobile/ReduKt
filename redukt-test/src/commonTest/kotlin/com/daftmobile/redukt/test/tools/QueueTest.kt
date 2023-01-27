package com.daftmobile.redukt.test.tools

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class QueueTest {


    @Test
    fun shouldCreateQueueWithInitialData() {
        queueOf(1, 2, 3) shouldContainExactly listOf(1, 2, 3)
    }

    @Test
    fun shouldCreateEmptyQueue() {
        queueOf<Int>().shouldBeEmpty()
    }

    @Test
    fun shouldAddElementToEmptyQueue() {
        val queue = queueOf<Int>()
        queue.push(0)
        queue shouldContainExactly listOf(0)
    }

    @Test
    fun shouldAddMultipleElements() {
        val queue = queueOf<Int>()
        queue.push(0)
        queue.push(1)
        queue.push(2)
        queue shouldContainExactly listOf(0, 1, 2)
    }

    @Test
    fun shouldReturnPulledElement() {
        val queue = queueOf(1, 2, 3)
        queue.pull() shouldBe 1
    }

    @Test
    fun shouldRemovePulledElement() {
        val queue = queueOf(1, 2, 3)
        queue.pull()
        queue shouldContainExactly listOf(2, 3)
    }

    @Test
    fun shouldRemoveTheOnlyOneElement() {
        val queue = queueOf(1)
        queue.pull()
        queue.shouldBeEmpty()
    }

    @Test
    fun shouldThrowWhenPullOnEmptyQueue() {
        shouldThrowAny {
            queueOf<Int>().pull()
        }
    }

    @Test
    fun shouldReturnNullWhenPulledOrNullOnEmptyQueue() {
        queueOf<Int>().pullOrNull().shouldBeNull()
    }

    @Test
    fun shouldReturnLastElementWhenPulledOrNull() {
        queueOf(0).pullOrNull() shouldBe 0
    }
}