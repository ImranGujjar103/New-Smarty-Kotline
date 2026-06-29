package com.imr.example.newsmartykotlin.presentation.premium

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.domain.model.PremiumLayoutType
import org.koin.androidx.compose.koinViewModel

@Composable
fun PremiumRoute(
    onCloseClick: () -> Unit,
    viewModel: PremiumViewModel = koinViewModel()
) {
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
                onContinueClick = viewModel::onContinueClick
            )
        }
    }
}