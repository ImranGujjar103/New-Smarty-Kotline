package com.imr.example.newsmartykotlin.presentation.language.components

import android.graphics.Typeface
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun LanguageTopAd(nativeAd: NativeAd?) {
    if (nativeAd == null) {
        Spacer(modifier = Modifier.height(72.dp))
    } else {
        LanguageNativeAd(
            nativeAd = nativeAd,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        )
    }
}

@Composable
fun LanguageBottomAd(nativeAd: NativeAd?) {
    if (nativeAd == null) {
        Spacer(modifier = Modifier.height(110.dp))
    } else {
        LanguageNativeAd(
            nativeAd = nativeAd,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun LanguageNativeAd(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NativeAdView(context).apply {
                val root = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(16, 10, 16, 10)
                }

                val icon = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(90, 90)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }

                val textColumn = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 0, 0, 0)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val headline = TextView(context).apply {
                    textSize = 15f
                    setTypeface(typeface, Typeface.BOLD)
                }

                val body = TextView(context).apply {
                    textSize = 12f
                    maxLines = 2
                }

                val cta = Button(context).apply {
                    text = "Install"
                }

                textColumn.addView(headline)
                textColumn.addView(body)
                textColumn.addView(cta)

                root.addView(icon)
                root.addView(textColumn)

                addView(root)

                iconView = icon
                headlineView = headline
                bodyView = body
                callToActionView = cta

                headline.text = nativeAd.headline
                body.text = nativeAd.body ?: ""
                cta.text = nativeAd.callToAction ?: "Install"

                nativeAd.icon?.drawable?.let {
                    icon.setImageDrawable(it)
                    icon.visibility = View.VISIBLE
                } ?: run {
                    icon.visibility = View.GONE
                }

                setNativeAd(nativeAd)
            }
        }
    )
}