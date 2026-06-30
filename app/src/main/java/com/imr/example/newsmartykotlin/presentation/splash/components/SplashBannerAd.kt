package com.imr.example.newsmartykotlin.presentation.splash.components
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.banner.AdView

@Composable
fun SplashBannerAd(
    adView: AdView,
    onBannerShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FrameLayout(context).apply {
                (adView.parent as? ViewGroup)?.removeView(adView)
                addView(
                    adView,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                )
                onBannerShown()
            }
        },
        update = { container ->
            if (adView.parent == null) {
                container.addView(adView)
                onBannerShown()
            }
        }
    )
}