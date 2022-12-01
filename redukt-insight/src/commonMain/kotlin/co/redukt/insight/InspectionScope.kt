package co.redukt.insight

import co.redukt.core.DispatchScope

public class InspectionScope<out State>(
    scope: DispatchScope<State>,
    public val action: co.redukt.core.Action,
): DispatchScope<State> by scope