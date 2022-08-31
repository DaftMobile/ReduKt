package com.daftmobile.redukt.core

import com.daftmobile.redukt.core.coroutines.DispatchCoroutineScope
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultsTest {
    @Test
    fun defaultStoreClosureShouldContainDispatchScope() {
        defaultStoreClosure(TestScope()).find(DispatchCoroutineScope) shouldNotBe null
    }
}