package com.imr.example.newsmartykotlin.presentation.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imr.example.newsmartykotlin.domain.usecase.premium.FetchBillingProductsUseCase
import com.imr.example.newsmartykotlin.domain.usecase.premium.ObservePremiumUiUseCase
import com.imr.example.newsmartykotlin.domain.usecase.premium.PurchasePremiumUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val observePremiumUiUseCase: ObservePremiumUiUseCase,
    private val fetchBillingProductsUseCase: FetchBillingProductsUseCase,
    private val purchasePremiumUseCase: PurchasePremiumUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeData()
        fetchProducts()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                observePremiumUiUseCase.repository.observePlans(),
                observePremiumUiUseCase.repository.observePremiumLayout(),
                observePremiumUiUseCase.repository.observePurchaseState()
            ) { plans, layout, purchased ->

                PremiumUiState(
                    isLoading = plans.any { it.price == "Loading..." },
                    layoutType = layout,
                    plans = plans,
                    selectedProductId = plans.firstOrNull { it.isSelected }?.productId.orEmpty(),
                    isPurchased = purchased
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            fetchBillingProductsUseCase()
        }
    }

    fun onPlanSelected(productId: String) {
        _uiState.update { state ->
            state.copy(
                selectedProductId = productId,
                plans = state.plans.map {
                    it.copy(isSelected = it.productId == productId)
                }
            )
        }
    }

    fun onContinueClick() {
        val productId = _uiState.value.selectedProductId
        if (productId.isEmpty()) return

        viewModelScope.launch {
            purchasePremiumUseCase(productId)
        }
    }
}