package com.daftmobile.redukt.support

import com.daftmobile.redukt.core.store.Selector

public abstract class SupportSelector<State, Selected> : Selector<State, Selected>

public fun <State, Selected> Selector<State, Selected>.toSupportSelector(): SupportSelector<State, Selected> {
    return object : SupportSelector<State, Selected>() {

        override fun select(state: State): Selected = this@toSupportSelector.select(state)

        override fun isStateEqual(
            old: State,
            new: State
        ): Boolean = this@toSupportSelector.isStateEqual(old, new)

        override fun isSelectionEqual(
            old: Selected,
            new: Selected
        ): Boolean = this@toSupportSelector.isSelectionEqual(old, new)
    }
}