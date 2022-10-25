package com.daftmobile.redukt.test.tools

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.coroutines.ForegroundJobRegistry
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

@DelicateReduKtApi
public class MockForegroundJobRegistry : ForegroundJobRegistry {
    override fun register(job: Job): Unit = Unit

    override fun consumeOrNull(): Job = Job().apply(CompletableJob::complete)

    override fun consume(): Job = consumeOrNull()
}