package com.imr.example.newsmartykotlin.presentation.suits

import com.imr.example.newsmartykotlin.domain.model.SuitCategory
import com.imr.example.newsmartykotlin.domain.model.SuitItem

data class SuitUiState(
    val isLoading: Boolean = true,
    val categories: List<SuitCategory> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val selectedItems: List<SuitItem> = emptyList()
)