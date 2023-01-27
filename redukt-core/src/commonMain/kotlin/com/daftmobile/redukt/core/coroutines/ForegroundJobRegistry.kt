package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlinx.coroutines.Job

/**
 * Allows to register a *foreground job*.
 * It is responsible for passing [Job] reference outside the store, so it can be awaited or cancelled.
 * By default, the store contains [EmptyForegroundJobRegistry] inside its closure.
 * Functions like [dispatchJob] use local closure mechanism to replace it with [SingleForegroundJobRegistry].
 * [launchForeground] directly refers to local [ForegroundJobRegistry].
 */
@DelicateReduKtApi
public interface ForegroundJobRegistry : DispatchClosure.Element {

    override val key: Key get() = Key

    /**
     * Registers a foreground job.
     */
    public fun register(job: Job)

    /**
     * Returns registered foreground job or null.
     */
    public fun consumeOrNull(): Job?

    /**
     * Returns registered foreground job or throws.
     */
    public fun consume(): Job

    public companion object Key : DispatchClosure.Key<ForegroundJobRegistry>
}

/**
 * A [ForegroundJobRegistry] that ignores registered job and always fails on [consume]. It's injected by default into the store's closure.
 */
@DelicateReduKtApi
public object EmptyForegroundJobRegistry : ForegroundJobRegistry {

    /**
     * Does nothing. [job] is ignored.
     */
    override fun register(job: Job): Unit = Unit

    /**
     * It only returns null.
     */
    override fun consumeOrNull(): Nothing? = null

    /**
     * It only throws.
     */
    override fun consume(): Nothing = error(
        "Foreground job could not be registered in EmptyForegroundJobRegistry! " +
                "You are probably using launchForeground outside of dispatch function!"
    )

    override fun toString(): String = "EmptyForegroundJobRegistry"
}

/**
 * A [ForegroundJobRegistry] that allows to register only one [Job] at once.
 * Repeated call to a [register] without a [consumeOrNull] results in an exception.
 */
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

    override fun toString(): String = "SingleForegroundJobRegistry(registeredJob=$job)"
}
