package com.imr.example.newsmartykotlin.presentation.suits

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
import com.imr.example.newsmartykotlin.domain.model.SuitItem
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuitRoute(
    onBackClick: () -> Unit,
    onNavigateToGallery: (SuitItem) -> Unit,
    viewModel: SuitViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as ComponentActivity

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.suitNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("SuitNative").collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        activity.setupLightSystemBars()
    }

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.suitNative.adId,
                tag = "SuitNative"
            ) { _ -> }
        }
    }

    SuitScreen(
        state = state,
        nativeState = nativeState,
        showAd = showAd,
        onBackClick = onBackClick,
        onCategoryClick = viewModel::onCategoryClick,
        onSuitClick = onNavigateToGallery
    )
}
