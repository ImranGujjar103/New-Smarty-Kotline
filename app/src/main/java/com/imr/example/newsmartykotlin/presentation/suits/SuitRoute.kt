package com.imr.example.newsmartykotlin.presentation.suits

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuitRoute(
    onBackClick: () -> Unit,
    viewModel: SuitViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SuitScreen(
        state = state,
        onBackClick = onBackClick,
        onCategoryClick = viewModel::onCategoryClick,
        onSuitClick = {
            // TODO selected suit image url
        }
    )
}