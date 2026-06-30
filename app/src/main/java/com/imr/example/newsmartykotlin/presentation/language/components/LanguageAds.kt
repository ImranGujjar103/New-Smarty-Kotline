package com.imr.example.newsmartykotlin.presentation.language.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.imr.example.newsmartykotlin.presentation.common.components.NativeAdMedium
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState

@Composable
fun LanguageBottomNativeAd(
    state: LanguageNativeState,
    modifier: Modifier = Modifier
) {
    val nativeAd = (state as? LanguageNativeState.Loaded)?.nativeAd
    NativeAdMedium(
        nativeAd = nativeAd,
        modifier = modifier
    )
}
