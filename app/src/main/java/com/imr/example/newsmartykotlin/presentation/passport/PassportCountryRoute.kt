package com.imr.example.newsmartykotlin.presentation.passport

import PassportCountryScreen
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import org.koin.androidx.compose.koinViewModel

@Composable
fun PassportCountryRoute(
    onBackClick: () -> Unit,
    onCountryClick: (PassportCountry, DocumentType) -> Unit,
    viewModel: PassportCountryViewModel = koinViewModel()
) {
    val countries by viewModel.countries.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    PassportCountryScreen(
        countries = countries,
        selectedType = selectedType,
        onTypeClick = viewModel::onTypeClick,
        onBackClick = onBackClick,
        onCountryClick = onCountryClick,
        onSearchChange = viewModel::search
    )
}