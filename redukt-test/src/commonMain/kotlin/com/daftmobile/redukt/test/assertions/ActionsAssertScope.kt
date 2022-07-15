package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.Queue

public interface ActionsAssertScope {

    public val history: List<Action>

    public val pipeline: Queue<Action>
}