package com.daftmobile.redukt.insight

public fun interface Insight<in State> {

    public fun Inspection<InspectionScope<State>>.intercept(): Inspection<Any?>

    public companion object {

        public fun default(tag: String = "ReduKt-Log >> "): Insight<Any?> = Insight {
            mapToActionWithTime()
                .prependWithThreadName()
                .prependWith(tag)
                .printToSystemOut()
        }

        public fun empty(): Insight<Any?> = Insight { this }
    }
}