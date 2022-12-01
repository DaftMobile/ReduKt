package co.redukt.test.tools

import co.redukt.core.DelicateReduKtApi
import co.redukt.core.coroutines.ForegroundJobRegistry
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

@DelicateReduKtApi
public class MockForegroundJobRegistry : ForegroundJobRegistry {
    override fun register(job: Job): Unit = Unit

    override fun consumeOrNull(): Job = Job().apply(CompletableJob::complete)

    override fun consume(): Job = consumeOrNull()
}