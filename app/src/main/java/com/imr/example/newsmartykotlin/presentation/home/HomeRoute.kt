package com.imr.example.newsmartykotlin.presentation.home

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.ads.AdLoadingState
import com.imr.example.newsmartykotlin.core.extensions.setupLightSystemBars
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    onNavigateToSuits: () -> Unit,
    onNavigateToBgChanger: () -> Unit,
    onNavigateToPassportPic: () -> Unit,
    onNavigateToMyCreation: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPremium: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as ComponentActivity

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.homeNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("HomeNative").collectAsStateWithLifecycle()
    val isAdDismissed by AdLoadingState.isAdDismissed.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        activity.setupLightSystemBars()
        viewModel.isFirstSplash(false)
        adViewModel.clearNativeAdStatesExcept("HomeNative")
    }

    LaunchedEffect(showAd, nativeState) {
        if (showAd && !isAdDismissed && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.homeNative.adId,
                tag = "HomeNative"
            ) { _ -> }
        }
    }

    LaunchedEffect(config.homeInterstitial.toShow) {
        if (config.homeInterstitial.toShow && !isPurchased && isConnected) {
            adViewModel.loadInterstitialAd(
                adId = config.homeInterstitial.adId,
                tag = "Home_Interstitial"
            )
        }
    }

    HomeScreen(
        state = state,
        nativeState = nativeState,
        showAd = showAd,
        onCrownClick = onNavigateToPremium,
        onSettingClick = onNavigateToSettings,
        onChangeClick = {
            adViewModel.showInterstitialAd(
                activity = activity,
                toShow = config.homeInterstitial.toShow,
                adId = config.homeInterstitial.adId,
                tag = "Home_Interstitial",
                callback = onNavigateToSuits
            )
        },
        onFeatureClick = { feature ->
            adViewModel.showInterstitialAd(
                activity = activity,
                toShow = config.homeInterstitial.toShow,
                adId = config.homeInterstitial.adId,
                tag = "Home_Interstitial",
                callback = {
                    when (feature.id) {
                        "passport_pic" -> onNavigateToPassportPic()
                        "bg_changer" -> onNavigateToBgChanger()
                        "my_creation" -> onNavigateToMyCreation()
                    }
                }
            )
        }
    )
}
