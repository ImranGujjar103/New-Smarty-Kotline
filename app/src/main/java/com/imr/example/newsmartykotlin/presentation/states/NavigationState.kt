package com.imr.example.newsmartykotlin.presentation.states

import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel


sealed interface NavigationState {
    data object Idle : NavigationState
    data class NavigateToLanguage(
        val showAd: Boolean,
        val adType: AdViewModel.AdType = AdViewModel.AdType.NONE
    ) : NavigationState
    data class NavigateToMain(
        val showAd: Boolean,
        val adType: AdViewModel.AdType = AdViewModel.AdType.NONE
    ) : NavigationState
    data class NavigateToPremium(
        val showAd: Boolean,
        val adType: AdViewModel.AdType = AdViewModel.AdType.NONE
    ) : NavigationState
    data object ADFailed : NavigationState
}