package dev.redukt.insight

import dev.redukt.core.DispatchScope

public class InspectionScope<out State>(
    scope: DispatchScope<State>,
    public val action: dev.redukt.core.Action,
): DispatchScope<State> by scope