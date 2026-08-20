package com.imr.example.newsmartykotlin.presentation.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.ads.AdLoadingState
import com.imr.example.newsmartykotlin.core.extensions.setupLightSystemBars
import com.imr.example.newsmartykotlin.presentation.home.components.ExitBottomSheet
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
    onNavigateToThankYou: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isAppRated by viewModel.isAppRated.collectAsStateWithLifecycle(initialValue = false)
    val context = LocalContext.current
    val activity = context as ComponentActivity

    var showExitSheet by remember { mutableStateOf(false) }

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()
    val isHomeInterstitialFirstTime by adViewModel.dataStorePrefs.isHomeInterstitialFirstTime().collectAsStateWithLifecycle(initialValue = true)

    val showAd = config.homeNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("HomeNative").collectAsStateWithLifecycle()
    val isAdDismissed by AdLoadingState.isAdDismissed.collectAsStateWithLifecycle()

    androidx.activity.compose.BackHandler {
        showExitSheet = true
    }

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

    LaunchedEffect(config.homeInterstitial.toShow, isHomeInterstitialFirstTime) {
        if (config.homeInterstitial.toShow && !isPurchased && isConnected && !isHomeInterstitialFirstTime) {
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
            if (isHomeInterstitialFirstTime) {
                viewModel.setHomeInterstitialFirstTime(false)
                onNavigateToSuits()
            } else {
                adViewModel.showInterstitialAd(
                    activity = activity,
                    toShow = config.homeInterstitial.toShow,
                    adId = config.homeInterstitial.adId,
                    tag = "Home_Interstitial",
                    callback = onNavigateToSuits
                )
            }
        },
        onFeatureClick = { feature ->
            if (isHomeInterstitialFirstTime) {
                viewModel.setHomeInterstitialFirstTime(false)
                when (feature.id) {
                    "passport_pic" -> onNavigateToPassportPic()
                    "bg_changer" -> onNavigateToBgChanger()
                    "my_creation" -> onNavigateToMyCreation()
                }
            } else {
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
        }
    )
    if (showExitSheet) {
        ExitBottomSheet(
            isRated = isAppRated,
            onDismiss = { showExitSheet = false },
            onExit = {
                showExitSheet = false
                if (isHomeInterstitialFirstTime) {
                    viewModel.setHomeInterstitialFirstTime(false)
                    onNavigateToThankYou()
                } else {
                    adViewModel.showInterstitialAd(
                        activity = activity,
                        toShow = config.homeInterstitial.toShow,
                        adId = config.homeInterstitial.adId,
                        tag = "Home_Interstitial",
                        callback = onNavigateToThankYou
                    )
                }
            },
            onRateUs = { rating ->
                showExitSheet = false
                viewModel.setAppRated(true)
                context.openPlayStore()
            }
        )
    }
}
fun Context.openPlayStore() {
    val packageName = packageName
    try {
        val intent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}