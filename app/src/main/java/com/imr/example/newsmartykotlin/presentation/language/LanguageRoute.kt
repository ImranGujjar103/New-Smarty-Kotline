package com.imr.example.newsmartykotlin.presentation.language

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.extensions.setupLightSystemBars
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LanguageRoute(
    fromSplash: Boolean,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBackClick: () -> Unit,
    languageViewModel: LanguageViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val uiState by languageViewModel.uiState.collectAsStateWithLifecycle()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.languageNative.toShow && !isPurchased && isConnected

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

    BackHandler {
        if (fromSplash) {
            onNavigateToOnboarding()
        } else {
            onBackClick()
        }
    }

    LanguageScreen(
        state = uiState,
        nativeState = nativeState,
        showAd = showAd,
        onLanguageClick = languageViewModel::onLanguageClick,
        onSaveClick = {
            languageViewModel.saveLanguage {
                if (fromSplash) {
                    onNavigateToOnboarding()
                } else {
                    onNavigateToHome()
                }
            }
        }
    )
}
