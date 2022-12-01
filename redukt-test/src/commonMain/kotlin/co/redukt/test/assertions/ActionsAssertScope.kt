package co.redukt.test.assertions

import co.redukt.core.Action
import co.redukt.test.tools.Queue

public interface ActionsAssertScope {

    public val history: List<Action>

    public val pipeline: Queue<Action>
}