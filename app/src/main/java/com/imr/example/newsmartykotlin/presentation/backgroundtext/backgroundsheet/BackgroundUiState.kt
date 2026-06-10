package com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet

import com.imr.example.newsmartykotlin.domain.model.BackgroundSection
import java.util.Collections.emptyList


data class BackgroundUiState(
    val isLoading: Boolean = false,
    val categories: List<BackgroundSection> = emptyList(),
    val error: String? = null
)