package com.daftmobile.redukt.mvvm

import kotlinx.coroutines.CoroutineScope

public expect abstract class CoreViewModel() {

    public val viewModelScope: CoroutineScope

    protected open fun onCleared()
}