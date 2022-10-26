package com.daftmobile.redukt.core

import com.daftmobile.redukt.test.middleware.MiddlewareTestScope
import com.daftmobile.redukt.test.middleware.MiddlewareTester
import kotlinx.coroutines.test.runTest

fun <T> MiddlewareTester<T>.runCoroutineTest(block: suspend MiddlewareTestScope<T>.() -> Unit) = test {
    runTest {
        block()
    }
}