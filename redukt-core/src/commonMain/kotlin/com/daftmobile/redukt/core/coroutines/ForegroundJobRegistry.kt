package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.Job

@DelicateReduKtApi
public interface ForegroundJobRegistry : DispatchClosure.Element {

    override val key: Key get() = Key

    public fun register(job: Job)

    public fun consumeOrNull(): Job?

    public fun consume(): Job

    public companion object Key : DispatchClosure.Key<ForegroundJobRegistry>
}

@DelicateReduKtApi
public class EmptyForegroundJobRegistry : ForegroundJobRegistry {
    override fun register(job: Job): Unit = Unit

    override fun consumeOrNull(): Job? = null

    override fun consume(): Job = error(
        "Foreground job could not be registered in EmptyForegroundJobRegistry! " +
                "You are probably using launchForeground outside of dispatch function!"
    )

}

@DelicateReduKtApi
public class SingleForegroundJobRegistry(
    private var job: Job? = null
) : ForegroundJobRegistry {

    override fun register(job: Job) {
        if (this.job != null) error("Foreground job already registered!")
        this.job = job
    }

    override fun consumeOrNull(): Job? = job.also { job = null }

    override fun consume(): Job = requireNotNull(consumeOrNull()) { "Foreground job not registered!" }
}
