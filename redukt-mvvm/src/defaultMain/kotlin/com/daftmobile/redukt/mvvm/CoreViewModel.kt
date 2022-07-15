package com.daftmobile.redukt.mvvm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

public actual abstract class CoreViewModel actual constructor() {

    public actual val viewModelScope: CoroutineScope = MainScope()

    protected actual open fun onCleared() {}

    public fun clear() {
        onCleared()
        viewModelScope.cancel()
    }
}