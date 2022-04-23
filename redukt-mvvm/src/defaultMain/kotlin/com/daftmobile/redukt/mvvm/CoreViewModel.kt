package com.daftmobile.redukt.mvvm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

actual abstract class CoreViewModel actual constructor() {

    actual val viewModelScope: CoroutineScope = MainScope()

    protected actual open fun onCleared() = Unit

    fun clear() {
        onCleared()
        viewModelScope.cancel()
    }
}