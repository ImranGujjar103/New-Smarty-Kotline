package com.imr.example.newsmartykotlin.presentation.onboarding

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.ads.AdLoadingController
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import com.imr.example.newsmartykotlin.core.extensions.isInternetAvailable

@Composable
fun OnboardingRoute(
    onNavigateToPremium: () -> Unit,
    onboardingViewModel: OnboardingViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel(),
    adLoadingController: AdLoadingController = koinInject()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    val nativeState by adViewModel.getNativeAdState("onboardingNative1").collectAsStateWithLifecycle()
    val config = adViewModel.adRepository.appConfig.collectAsState().value

    LaunchedEffect(Unit) {
        if (config.onBoardingInterstitial.toShow && context.isInternetAvailable()){
            adViewModel.loadInterstitialAd(
                adId = adViewModel.adRepository.appConfig.value.onBoardingInterstitial.adId,
                tag = "Onboarding_Interstitial"
            )
        }


        if (config.onboardingNative1.toShow && context.isInternetAvailable()) {
            adViewModel.loadNativeAd(
                adId = config.onboardingNative1.adId,
                tag = "onboardingNative1"
            ) { _ -> }
        }
    }

    fun handleNextClick() {
        val isLastPage = uiState.currentPage == uiState.pages.lastIndex

        if (isLastPage) {
            adViewModel.showInterstitialAdWithCallBack(
                activity = activity,
                toShow = adViewModel.adRepository.appConfig.value.onBoardingInterstitial.toShow,
                tag = "Onboarding_Interstitial",
                adId = adViewModel.adRepository.appConfig.value.onBoardingInterstitial.adId,
                adLoadingController = adLoadingController,
                callback = onNavigateToPremium
            )
        } else {
            onboardingViewModel.onPageChanged(uiState.currentPage + 1)
        }
    }

    OnboardingScreen(
        uiState = uiState,
        nativeState = nativeState,
        onPageChanged = onboardingViewModel::onPageChanged,
        onNextClick = ::handleNextClick
    )
}