package com.imr.example.newsmartykotlin.core.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdLoadingState {

    private val _isShowing = MutableStateFlow(false)
    val isShowing: StateFlow<Boolean> = _isShowing.asStateFlow()

    private val _isInterstitialShowing = MutableStateFlow(false)
    val isInterstitialShowing: StateFlow<Boolean> = _isInterstitialShowing.asStateFlow()

    private val _isAdDismissed = MutableStateFlow(false)
    val isAdDismissed: StateFlow<Boolean> = _isAdDismissed.asStateFlow()

    fun show() {
        _isShowing.value = true
        _isAdDismissed.value = false
    }

    fun hide() {
        _isShowing.value = false
    }

    fun setAdDismissed(dismissed: Boolean) {
        _isAdDismissed.value = dismissed
    }

    fun setInterstitialShowing(showing: Boolean) {
        _isInterstitialShowing.value = showing
    }
}