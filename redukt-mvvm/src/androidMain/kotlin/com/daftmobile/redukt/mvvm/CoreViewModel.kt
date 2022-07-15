package com.daftmobile.redukt.mvvm

import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope

public actual abstract class CoreViewModel actual constructor() : AndroidXViewModel() {

    public actual val viewModelScope: CoroutineScope = androidXViewModelScope

    protected actual override fun onCleared() {
        super.onCleared()
    }
}