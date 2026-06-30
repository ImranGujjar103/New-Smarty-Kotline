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

    val nativeState by adViewModel.getNativeAdState("HomeBottomNative").collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        activity.setupLightSystemBars()
        viewModel.isFirstSplash(false)
    }

    LaunchedEffect(showAd) {
        if (showAd) {
            adViewModel.loadNativeAd(
                adId = config.homeNative.adId,
                tag = "HomeBottomNative"
            ) { _ -> }
        }
    }

    HomeScreen(
        state = state,
        nativeState = nativeState,
        showAd = showAd,
        onCrownClick = onNavigateToPremium,
        onSettingClick = onNavigateToSettings,
        onChangeClick = onNavigateToSuits,
        onFeatureClick = { feature ->
            when (feature.id) {
                "passport_pic" -> onNavigateToPassportPic()
                "bg_changer" -> onNavigateToBgChanger()
                "my_creation" -> onNavigateToMyCreation()
            }
        }
    )
}
