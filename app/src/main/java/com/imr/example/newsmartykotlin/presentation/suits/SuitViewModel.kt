package com.imr.example.newsmartykotlin.presentation.suits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.suits.GetSuitCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SuitViewModel(
    private val getSuitCategoriesUseCase: GetSuitCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuitUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSuits()
    }

    private fun loadSuits() {
        viewModelScope.launch {
            val categories = getSuitCategoriesUseCase()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    categories = categories,
                    selectedCategoryIndex = 0,
                    selectedItems = categories.firstOrNull()?.items.orEmpty()
                )
            }
        }
    }

    fun onCategoryClick(index: Int) {
        val category = _uiState.value.categories.getOrNull(index) ?: return

        _uiState.update {
            it.copy(
                selectedCategoryIndex = index,
                selectedItems = category.items
            )
        }
    }
}