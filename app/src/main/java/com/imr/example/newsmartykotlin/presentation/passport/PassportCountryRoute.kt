package com.imr.example.newsmartykotlin.presentation.passport

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PassportCountryRoute(
    onBackClick: () -> Unit,
    onCountryClick: (PassportCountry, DocumentType) -> Unit,
    viewModel: PassportCountryViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val countries by viewModel.countries.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.passportCountryNative.toShow && !isPurchased && isConnected

    var nativeState by remember { mutableStateOf<LanguageNativeState>(LanguageNativeState.Idle) }

    LaunchedEffect(showAd) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            nativeState = LanguageNativeState.Loading
            adViewModel.loadNativeAd(
                adId = config.passportCountryNative.adId,
                tag = "PassportCountryBottomNative"
            ) { ad ->
                nativeState = if (ad != null) {
                    LanguageNativeState.Loaded(ad)
                } else {
                    LanguageNativeState.Failed
                }
            }
        }
    }

    PassportCountryScreen(
        countries = countries,
        selectedType = selectedType,
        nativeState = nativeState,
        showAd = showAd,
        onTypeClick = viewModel::onTypeClick,
        onBackClick = onBackClick,
        onCountryClick = onCountryClick,
        onSearchChange = viewModel::search
    )
}