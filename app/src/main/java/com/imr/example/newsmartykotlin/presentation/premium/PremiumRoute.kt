package com.imr.example.newsmartykotlin.presentation.premium

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.extensions.setupLightSystemBars
import com.imr.example.newsmartykotlin.core.utils.BillingManager
import com.imr.example.newsmartykotlin.domain.model.PremiumLayoutType
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PremiumRoute(
    onCloseClick: () -> Unit,
    viewModel: PremiumViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val billingManager: BillingManager = koinInject()

    LaunchedEffect(activity) {
        activity.setupLightSystemBars()
    }

    LaunchedEffect(viewModel.purchaseEvent) {
        viewModel.purchaseEvent.collect { productId ->
            billingManager.purchaseSubscription(activity, productId)
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler {
        onCloseClick()
    }

    when (state.layoutType) {
        PremiumLayoutType.TWO_CARD -> {
            PremiumTwoCardScreen(
                state = state,
                onCloseClick = onCloseClick,
                onPlanClick = viewModel::onPlanSelected,
                onContinueClick = viewModel::onContinueClick
            )
        }

        PremiumLayoutType.SINGLE_CARD -> {
            PremiumSingleCardScreen(
                state = state,
                onCloseClick = onCloseClick,
                onPlanClick = viewModel::onPlanSelected,
                onContinueClick = viewModel::onContinueClick,
                onPurchaseClick = viewModel::purchaseProduct
            )
        }
    }
}