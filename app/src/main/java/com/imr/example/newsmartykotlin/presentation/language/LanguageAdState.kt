package com.imr.example.newsmartykotlin.presentation.language

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd

sealed interface LanguageBannerState {
    data object Idle : LanguageBannerState
    data object Loading : LanguageBannerState
    data object Failed : LanguageBannerState
    data class Loaded(val adView: AdView) : LanguageBannerState
}

sealed interface LanguageNativeState {
    data object Idle : LanguageNativeState
    data object Loading : LanguageNativeState
    data object Failed : LanguageNativeState
    data class Loaded(val nativeAd: NativeAd) : LanguageNativeState
}