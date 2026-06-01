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
        onCrownClick = {
            // TODO Premium Screen
        },
        onSettingClick = {
            // TODO Settings Screen
        },
        onChangeClick = onNavigateToSuits,
        onFeatureClick = { feature ->
            when (feature.id) {

                "face_swap" -> {
                    // TODO Navigate Face Swap
                }

                "passport_pic" -> {
                    // TODO Navigate Passport Pic
                }

                "bg_changer" -> {
                    // TODO Navigate BG Changer
                }

                "my_creation" -> {
                    // TODO Navigate My Creation
                }
            }
        }
    )
}