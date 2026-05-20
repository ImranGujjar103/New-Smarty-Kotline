package com.imr.example.newsmartykotlin.presentation.language.components

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.imr.example.newsmartykotlin.presentation.language.LanguageBannerState
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState

@Composable
fun LanguageTopBannerAd(
    state: LanguageBannerState,
    modifier: Modifier = Modifier
) {
    when (state) {
        LanguageBannerState.Loading -> {
            AdShimmerBox(
                modifier = modifier
                    .height(72.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        is LanguageBannerState.Loaded -> {
            AndroidView(
                modifier = modifier
                    .height(72.dp)
                    .padding(horizontal = 8.dp),
                factory = { context ->
                    FrameLayout(context).apply {
                        (state.adView.parent as? ViewGroup)?.removeView(state.adView)
                        addView(
                            state.adView,
                            FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.CENTER
                            }
                        )
                    }
                },
                update = { container ->
                    (state.adView.parent as? ViewGroup)?.removeView(state.adView)
                    container.removeAllViews()
                    container.addView(state.adView)
                }
            )
        }

        else -> Unit
    }
}

@Composable
fun LanguageBottomNativeAd(
    state: LanguageNativeState,
    modifier: Modifier = Modifier
) {
    when (state) {
        LanguageNativeState.Loading -> {
            AdShimmerBox(
                modifier = modifier
                    .height(125.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        is LanguageNativeState.Loaded -> {
            LanguageNativeAd(
                nativeAd = state.nativeAd,
                modifier = modifier
                    .height(125.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        else -> Unit
    }
}

@Composable
fun AdShimmerBox(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "adShimmer")

    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "adShimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim, 300f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(brush)
    )
}