package com.imr.example.newsmartykotlin.core.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdLoadingState {

    private val _isShowing = MutableStateFlow(false)
    val isShowing: StateFlow<Boolean> = _isShowing.asStateFlow()

    private val _isInterstitialShowing = MutableStateFlow(false)
    val isInterstitialShowing: StateFlow<Boolean> = _isInterstitialShowing.asStateFlow()

    fun show() {
        _isShowing.value = true
    }

    fun hide() {
        _isShowing.value = false
    }

    fun setInterstitialShowing(showing: Boolean) {
        _isInterstitialShowing.value = showing
    }
}