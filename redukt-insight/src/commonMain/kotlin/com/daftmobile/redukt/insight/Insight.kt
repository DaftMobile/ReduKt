package com.daftmobile.redukt.insight

public fun interface Insight<in State> {

    public fun Inspection<InspectionScope<State>>.intercept(): Inspection<Any?>

    public companion object {
        public fun empty(): Insight<Any?> = Insight { this }

        public fun debug(tag: String = "ReduKt-Insight: ", showTime: Boolean = true): Insight<Any?> = Insight {
            val inspection = if (showTime) mapToActionWithTime() else map { it.action.toString() }
            inspection
                .prependWith(tag)
                .splitByNewLine()
                .printToSystemOut()
        }

        public fun <State> sharedBetween(vararg insights: Insight<State>): Insight<State> = Insight {
            val inspectors = insights.map(Insight<State>::toInspector)
            onEach {
                inspectors.forEach { inspector -> inspector.inspect(it) }
            }
        }
    }
}