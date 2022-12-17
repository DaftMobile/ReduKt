package dev.redukt.test.tools

import dev.redukt.core.DelicateReduKtApi
import dev.redukt.core.coroutines.ForegroundJobRegistry
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

/**
 * [ForegroundJobRegistry] that ignores interactions and returns a completed Job on consume.
 */
@DelicateReduKtApi
public class TestForegroundJobRegistry : ForegroundJobRegistry {
    override fun register(job: Job): Unit = Unit

    override fun consumeOrNull(): Job = Job().apply(CompletableJob::complete)

    override fun consume(): Job = consumeOrNull()
}