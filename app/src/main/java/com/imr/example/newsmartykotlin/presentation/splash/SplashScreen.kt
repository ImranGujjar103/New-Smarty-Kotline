package com.imr.example.newsmartykotlin.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdView
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.splash.components.SplashBannerAd
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBlackItalic
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor

@Composable
fun SplashScreen(
    showBanner: Boolean,
    bannerAdView: AdView?,
    onBannerShown: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {

        Image(
            painter = painterResource(id = R.drawable.img_splash_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.67f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.50f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White,
                            Color.White,
                            Color.White
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))


            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.welcome),
                style = TextStyle(
                    fontFamily = SfProDisplayBlackItalic,
                    fontSize = 30.sp,
                    color = PrimaryColor,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.to_suit_changer),
                style = TextStyle(
                    fontFamily = SfProDisplayBold,
                    fontSize = 16.sp,
                    color = TextColor,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(46.dp))

            CircularProgressIndicator(
                color = PrimaryColor,
                strokeWidth = 5.dp,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (showBanner && bannerAdView != null) {
                SplashBannerAd(
                    adView = bannerAdView,
                    onBannerShown = onBannerShown
                )
            } else {
                Spacer(modifier = Modifier.height(60.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}