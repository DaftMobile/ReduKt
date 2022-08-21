package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.InternalReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosure
import kotlinx.coroutines.Job

@InternalReduKtApi
public interface ForegroundJobRegistry : DispatchClosure.Element {

    override val key: Key get() = Key

    public fun register(job: Job)

    public fun consumeOrNull(): Job?

    public fun consume(): Job

    public companion object Key : DispatchClosure.Key<ForegroundJobRegistry>
}

@InternalReduKtApi
public class EmptyForegroundJobRegistry : ForegroundJobRegistry {
    override fun register(job: Job): Unit = Unit

    override fun consumeOrNull(): Job? = null

    override fun consume(): Job = error(
        "Foreground job could not be registered in EmptyForegroundJobRegistry! " +
                "You are probably using launchForeground outside of DispatchFunction!"
    )

}

@InternalReduKtApi
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

@InternalReduKtApi
public val LocalClosure.foregroundJobRegistry: ForegroundJobRegistry get() = get(ForegroundJobRegistry)
