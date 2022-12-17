package dev.redukt.test.assertions

import dev.redukt.core.Action
import dev.redukt.test.tools.Queue

/**
 * It is a core API for all assertions. It provides all actions dispatched with a spying component.
 * If assertion verifies certain action, it is responsible for pulling it from [unverified] queue.
 * Queue enforces verifying actions from the oldest to the latest.
 * Verifying actions is expected behaviour of every assertion,
 * but there are assertions that check certain conditions without pulling from the queue.
 * Also, action might be skipped. It means that it is removed from [unverified] without assertion.
 *
 * If you want to provide your own assertion check existing assertions source code.
 */
public interface ActionsAssertScope {

    /**
     * Contains all dispatched actions history.
     */
    public val history: List<Action>

    /**
     * Contains all dispatched actions queue. If action is verified, it must be pulled from this queue.
     */
    public val unverified: Queue<Action>
}