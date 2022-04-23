package com.daftmobile.redukt.mvvm

import kotlinx.coroutines.CoroutineScope

expect abstract class CoreViewModel() {

    val viewModelScope: CoroutineScope

    protected open fun onCleared()
}