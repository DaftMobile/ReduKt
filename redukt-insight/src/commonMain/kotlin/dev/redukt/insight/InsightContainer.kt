package dev.redukt.insight

import dev.redukt.core.DelicateReduKtApi
import dev.redukt.core.DispatchScope
import dev.redukt.core.closure.DispatchClosure
import dev.redukt.insight.InsightContainer.ValueKey
import kotlin.jvm.JvmInline
import kotlin.time.Duration

@Suppress("UNCHECKED_CAST")
@DelicateReduKtApi
public class InsightContainer: DispatchClosure.Element {

    private val values = mutableMapOf<ValueKey<*>, Any?>()

    public operator fun <T> set(key: ValueKey<T>, value: T) {
        values[key] = value
    }

    public operator fun <T> get(key: ValueKey<T>): T = values[key] as T

    override val key: Key = Key

    public companion object Key : DispatchClosure.Key<InsightContainer>

    override fun toString(): String = "InsightContainer($values)"

    @JvmInline
    public value class ValueKey<T>(public val key: String) {
        override fun toString(): String = key
    }
}

public object InsightValues {

    public val PreviousState: ValueKey<Any?> = ValueKey("previousState")

    public val ProcessedActionTime: ValueKey<Duration?> = ValueKey("processedActionTime")
}

@PublishedApi
internal fun <T> Insight<T>.toInspector(): Inspector<InspectionScope<T>> {
    var scope: InspectionScope<T>? = null
    val inspection = inspection { inspect(scope!!) }.intercept()
    return Inspector {
        scope = it
        inspection.inspect()
    }
}

public val DispatchScope<*>.insightContainer: InsightContainer get() = closure[InsightContainer]
