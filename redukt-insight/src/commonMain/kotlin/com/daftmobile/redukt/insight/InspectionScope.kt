package com.daftmobile.redukt.insight

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.core.DispatchScope

public class InspectionScope<out State>(
    scope: DispatchScope<State>,
    public val action: Action,
): DispatchScope<State> by scope