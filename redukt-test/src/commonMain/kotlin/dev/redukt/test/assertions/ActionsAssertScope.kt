package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.Queue

/**
 * Provides all actions dispatched with a spying component. It is a core API for all assertions.
 * If assertion logically consumes/verifies certain action, it is responsible for pulling it from [unverified] queue.
 * Most of the assertions consume actions. If given assertion does not consume, it should be mentioned in the documentation.
 * Action might be skipped. It means that it is removed from [unverified] without assertion.
 */
public interface ActionsAssertScope {

    /**
     * Contains all dispatched actions history.
     */
    public val history: List<Action>

    /**
     * Contains all dispatched actions queue.
     */
    public val unverified: Queue<Action>
}