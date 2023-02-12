package com.daftmobile.redukt.core.coroutines

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.closure.DispatchClosure
import com.daftmobile.redukt.core.closure.EmptyDispatchClosure
import com.daftmobile.redukt.core.closure.LocalClosureContainer
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

/**
 * Creates a [DispatchClosure] that skips coroutines mechanism. Every [dispatchJob] is executed with no failure and returns completed job.
 */
@DelicateReduKtApi
public fun skipCoroutinesClosure(): DispatchClosure = DisabledLocalClosureContainer() + DisabledForegroundJobRegistry

/**
 * Provides [LocalClosureContainer] that ignores local changes and returns [closure].
 */
@DelicateReduKtApi
public class DisabledLocalClosureContainer(
    private val closure: DispatchClosure = EmptyDispatchClosure
) : LocalClosureContainer {

    override fun applyTo(closure: DispatchClosure): DispatchClosure = closure + this.closure

    override fun registerNewSlot(closure: DispatchClosure): LocalClosureContainer.Slot = LocalClosureContainer.Slot()

    override fun removeSlot(slot: LocalClosureContainer.Slot): Unit = Unit
    override fun registerNewFrame(): LocalClosureContainer.Frame = LocalClosureContainer.Frame()

    override fun removeFrame(frame: LocalClosureContainer.Frame): Unit = Unit
}

/**
 * [ForegroundJobRegistry] that ignores interactions and returns a completed Job on consume.
 */
@DelicateReduKtApi
public object DisabledForegroundJobRegistry : ForegroundJobRegistry {
    override fun register(job: Job): Unit = Unit

    override fun consumeOrNull(): Job = Job().apply(CompletableJob::complete)

    override fun consume(): Job = consumeOrNull()
}