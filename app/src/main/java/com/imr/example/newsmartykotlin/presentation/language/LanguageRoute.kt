package com.imr.example.newsmartykotlin.presentation.language

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.ads.AdLoadingController
import com.imr.example.newsmartykotlin.core.ads.AdManager
import com.imr.example.newsmartykotlin.core.extensions.setupLightSystemBars
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LanguageRoute(
    fromSplash: Boolean,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBackClick: () -> Unit,
    languageViewModel: LanguageViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel(),
    adLoadingController: AdLoadingController = koinInject()
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val uiState by languageViewModel.uiState.collectAsStateWithLifecycle()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.languageNative.toShow && !isPurchased && isConnected
    val interConfig = config.languageInterstitial
    val isInterEnabled = interConfig.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("LanguageNative").collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        activity.setupLightSystemBars()
    }

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.languageNative.adId,
                tag = "LanguageNative"
            ) { _ -> }
        }
    }

    LaunchedEffect(isInterEnabled) {
        if (isInterEnabled) {
            adViewModel.loadInterstitialAd(
                adId = interConfig.adId,
                tag = "LanguageInter"
            )
        }
    }

    val handleSave = {
        languageViewModel.saveLanguage {
            val isInterCached = AdManager.isInterstitialCached()
            if (isInterEnabled && isInterCached) {
                adViewModel.showInterstitialAdWithCallBack(
                    activity = activity,
                    toShow = true,
                    tag = "LanguageInter",
                    adId = interConfig.adId,
                    adLoadingController = adLoadingController,
                    delayMillis = 500L,
                    callback = {
                        if (fromSplash) {
                            onNavigateToOnboarding()
                        } else {
                            onNavigateToHome()
                        }
                    }
                )
            } else {
                if (fromSplash) {
                    onNavigateToOnboarding()
                } else {
                    onNavigateToHome()
                }
            }
        }
    }

    BackHandler {
        if (fromSplash) {
            handleSave()
        } else {
            onBackClick()
        }
    }

    LanguageScreen(
        state = uiState,
        nativeState = nativeState,
        showAd = showAd,
        onLanguageClick = languageViewModel::onLanguageClick,
        onSaveClick = handleSave
    )
}
