package com.imr.example.newsmartykotlin.presentation.common.components

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import com.imr.example.newsmartykotlin.R

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

@Composable
fun NativeAdMedium(
    nativeAd: NativeAd?,
    modifier: Modifier = Modifier
) {
    if (nativeAd == null) {
        AdShimmerBox(
            modifier = modifier
                .fillMaxWidth()
                .height(134.dp)
        )
        return
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(androidx.compose.ui.graphics.Color.White),
        factory = { context ->
            NativeAdView(context).apply {
                val root = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(dpToPx(context, 12), dpToPx(context, 12), dpToPx(context, 12), dpToPx(context, 12))
                    gravity = Gravity.CENTER_VERTICAL
                }

                // Left: MediaView (120x120 dp)
                val adMediaView = MediaView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(dpToPx(context, 120), dpToPx(context, 120))
                    outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
                    clipToOutline = true
                    background = android.graphics.drawable.GradientDrawable().apply {
                        cornerRadius = dpToPx(context, 10).toFloat()
                        setColor(Color.LTGRAY)
                    }
                }

                // Right: Content
                val rightColumn = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                        marginStart = dpToPx(context, 12)
                    }
                }

                // Row 1: AD + Headline
                val titleRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }
                val adBadge = TextView(context).apply {
                    text = "AD"
                    textSize = 10f
                    setTextColor(0xFF333333.toInt())
                    setPadding(dpToPx(context, 4), 0, dpToPx(context, 4), 0)
                    val gd = android.graphics.drawable.GradientDrawable().apply {
                        setStroke(dpToPx(context, 1), 0xFF333333.toInt())
                        cornerRadius = dpToPx(context, 3).toFloat()
                    }
                    background = gd
                }
                val headline = TextView(context).apply {
                    textSize = 15f
                    setTextColor(Color.BLACK)
                    setTypeface(null, Typeface.BOLD)
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = dpToPx(context, 6)
                    }
                }
                titleRow.addView(adBadge)
                titleRow.addView(headline)

                // Row 2: Small Icon + Body
                val bodyRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = dpToPx(context, 8)
                    }
                }
                val smallIcon = ImageView(context).apply {
                    val size = dpToPx(context, 30)
                    layoutParams = LinearLayout.LayoutParams(size, size)
                    scaleType = ImageView.ScaleType.CENTER_CROP

                    outlineProvider = ViewOutlineProvider.BACKGROUND
                    clipToOutline = true
                    background = GradientDrawable().apply {
                        cornerRadius = dpToPx(context, 8).toFloat()
                    }
                }
                val bodyText = TextView(context).apply {
                    textSize = 12f
                    setTextColor(0xFF666666.toInt())
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        marginStart = dpToPx(context, 8)
                    }
                }
                bodyRow.addView(smallIcon)
                bodyRow.addView(bodyText)

                // CTA
                val cta = Button(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(context, 40)
                    ).apply {
                        topMargin = dpToPx(context, 12)
                    }
                    val gd = android.graphics.drawable.GradientDrawable().apply {
                        setColor(0xFF5C8EF2.toInt())
                        cornerRadius = dpToPx(context, 8).toFloat()
                    }
                    background = gd
                    setTextColor(Color.WHITE)
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    isAllCaps = false
                    setPadding(0, 0, 0, 0)
                }

                rightColumn.addView(titleRow)
                rightColumn.addView(bodyRow)
                rightColumn.addView(cta)

                root.addView(adMediaView)
                root.addView(rightColumn)

                addView(root)

                this.headlineView = headline
                this.bodyView = bodyText
                this.callToActionView = cta
                this.iconView = smallIcon
                
                headline.text = nativeAd.headline
                bodyText.text = nativeAd.body ?: ""
                cta.text = nativeAd.callToAction ?: context.getString(R.string.install)

                nativeAd.icon?.drawable?.let {
                    smallIcon.setImageDrawable(it)
                }

                registerNativeAd(nativeAd, adMediaView)
            }
        }
    )
}

private fun dpToPx(context: android.content.Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}
