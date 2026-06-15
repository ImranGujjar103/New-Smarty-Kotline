package com.imr.example.newsmartykotlin.presentation.home

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.core.extensions.setupLightSystemBars
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    onNavigateToSuits: () -> Unit,
    onNavigateToBgChanger: () -> Unit,
    onNavigateToPassportPic: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as ComponentActivity

    LaunchedEffect(Unit) {
        activity.setupLightSystemBars()
    }

    HomeScreen(
        state = state,
        onCrownClick = {},
        onSettingClick = {},
        onChangeClick = onNavigateToSuits,
        onFeatureClick = { feature ->
            when (feature.id) {
                "passport_pic" -> onNavigateToPassportPic()
                "bg_changer" -> onNavigateToBgChanger()
                "face_swap" -> {}
                "my_creation" -> {}
            }
        }
    )
}