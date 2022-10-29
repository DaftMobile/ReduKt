package com.daftmobile.redukt.insight

import com.daftmobile.redukt.core.DelicateReduKtApi
import com.daftmobile.redukt.core.DispatchScope
import com.daftmobile.redukt.core.closure.DispatchClosure
import kotlin.time.Duration

@DelicateReduKtApi
public class InsightContainer: DispatchClosure.Element {

    public var previousState: Any? = null

    public var processedActionTime: Duration? = null

    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<InsightContainer>
}

public val DispatchScope<*>.insightContainer: InsightContainer get() = closure[InsightContainer]
