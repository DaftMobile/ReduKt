package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.closure.DispatchClosure

/**
 * Creates a [DispatchClosure] that mocks foreground coroutines mechanism. Every [com.daftmobile.redukt.core.coroutines.dispatchJob]
 * is executed with no failure and returns completed job.
 */
public fun SkipCoroutinesClosure(): DispatchClosure = TestLocalClosureContainer() + TestForegroundJobRegistry()