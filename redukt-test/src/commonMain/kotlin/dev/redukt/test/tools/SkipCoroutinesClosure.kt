package dev.redukt.test.tools

import dev.redukt.core.DelicateReduKtApi
import dev.redukt.core.closure.DispatchClosure

/**
 * Creates a [DispatchClosure] that mocks foreground coroutines mechanism. Every [dev.redukt.core.coroutines.dispatchJob]
 * is executed with no failure and returns completed job.
 */
@DelicateReduKtApi
public fun SkipCoroutinesClosure(): DispatchClosure = TestLocalClosureContainer() + TestForegroundJobRegistry()