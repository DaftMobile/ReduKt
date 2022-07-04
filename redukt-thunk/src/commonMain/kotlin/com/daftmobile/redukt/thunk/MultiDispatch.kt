package com.daftmobile.redukt.thunk

import com.daftmobile.redukt.core.Action

data class MultiDispatch(val actions: List<Action>): Thunk<Nothing?>({
    actions.forEach { dispatch(it) }
})