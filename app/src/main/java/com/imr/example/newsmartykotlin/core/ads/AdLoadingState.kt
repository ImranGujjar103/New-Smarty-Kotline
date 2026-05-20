package com.imr.example.newsmartykotlin.core.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdLoadingState {

    private val _isShowing = MutableStateFlow(false)
    val isShowing: StateFlow<Boolean> = _isShowing.asStateFlow()

    fun show() {
        _isShowing.value = true
    }

    fun hide() {
        _isShowing.value = false
    }
}