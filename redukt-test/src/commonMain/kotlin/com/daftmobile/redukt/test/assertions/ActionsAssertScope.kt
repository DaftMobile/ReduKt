package com.daftmobile.redukt.test.assertions

import com.daftmobile.redukt.core.Action
import com.daftmobile.redukt.test.tools.Queue

interface ActionsAssertScope {

    val history: List<Action>

    val pipeline: Queue<Action>
}