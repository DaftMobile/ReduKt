package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.Queue

public interface ActionsAssertScope {

    public val history: List<Action>

    public val pipeline: Queue<Action>
}