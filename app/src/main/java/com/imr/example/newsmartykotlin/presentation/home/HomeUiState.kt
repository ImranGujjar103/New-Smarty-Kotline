package com.imr.example.newsmartykotlin.presentation.home

import com.imr.example.newsmartykotlin.domain.model.HomeFeature

data class HomeUiState(
    val features: List<HomeFeature> = emptyList()
)