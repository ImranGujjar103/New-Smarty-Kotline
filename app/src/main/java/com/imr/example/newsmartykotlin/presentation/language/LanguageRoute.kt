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

    var nativeState by remember { mutableStateOf<LanguageNativeState>(LanguageNativeState.Idle) }

    LaunchedEffect(Unit) {
        activity.setupLightSystemBars()
        val config = adViewModel.adRepository.appConfig.value

        if (config.languageNative.toShow) {
            nativeState = LanguageNativeState.Loading
            adViewModel.loadNativeAd(
                adId = config.languageNative.adId,
                tag = "LanguageBottomNative"
            ) { ad ->
                nativeState = if (ad != null) {
                    LanguageNativeState.Loaded(ad)
                } else {
                    LanguageNativeState.Failed
                }
            }
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