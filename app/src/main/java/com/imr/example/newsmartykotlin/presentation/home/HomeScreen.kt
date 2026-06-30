package com.imr.example.newsmartykotlin.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.HomeFeature
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun HomeScreen(
    state: HomeUiState,
    nativeState: LanguageNativeState,
    showAd: Boolean,
    onCrownClick: () -> Unit,
    onSettingClick: () -> Unit,
    onChangeClick: () -> Unit,
    onFeatureClick: (HomeFeature) -> Unit
) {


    HomeContent(
        state = state,
        nativeState = nativeState,
        showAd = showAd,
        onCrownClick = onCrownClick,
        onSettingClick = onSettingClick,
        onChangeClick = onChangeClick,
        onFeatureClick = onFeatureClick
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    nativeState: LanguageNativeState,
    showAd: Boolean,
    onCrownClick: () -> Unit,
    onSettingClick: () -> Unit,
    onChangeClick: () -> Unit,
    onFeatureClick: (HomeFeature) -> Unit
) {
    val gridFeatures = remember(state.features) {
        state.features.filter { it.id == "bg_changer" || it.id == "passport_pic" }
    }
    val myCreationFeature = remember(state.features) {
        state.features.find { it.id == "my_creation" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HomeBackgroundColor,
                        HomeBackgroundColor
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            HomeTopBar(
                onCrownClick = onCrownClick,
                onSettingClick = onSettingClick
            )

            Spacer(modifier = Modifier.height(25.dp))

            SuitChangerCard(
                onChangeClick = onChangeClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            FeatureGrid(
                features = gridFeatures,
                onFeatureClick = onFeatureClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            myCreationFeature?.let { feature ->
                MyCreationCard(
                    feature = feature,
                    onClick = { onFeatureClick(feature) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showAd) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                LanguageBottomNativeAd(
                    state = nativeState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    onCrownClick: () -> Unit,
    onSettingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.suit),
                fontFamily = SfProDisplayBold,
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                color = PrimaryColor
            )

            Text(
                text = stringResource(R.string.changer),
                fontFamily = SfProDisplayBold,
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                color = TextColor
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_premium_crown),
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onCrownClick()
                }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onSettingClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = null,
                tint = WhiteColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SuitChangerCard(
    onChangeClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(24.dp),
        color = WhiteColor,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.img_suit_changer_man),
                contentDescription = null,
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight(),
                contentScale = ContentScale.FillHeight
            )

            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .padding(end = 20.dp, start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.suit_changer),
                    fontFamily = SfProDisplayBold,
                    fontSize = 20.sp,
                    lineHeight = 14.sp,
                    color = TextColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.suit_changer_subtitle),
                    fontFamily = SfProDisplayRegular,
                    fontSize = 14.sp,
                    lineHeight = 13.sp,
                    color = TextColor.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    onClick = onChangeClick,
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = WhiteColor
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.lets_change),
                            fontFamily = SfProDisplayBold,
                            fontSize = 14.sp,
                            color = WhiteColor
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = null,
                            tint = WhiteColor,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyCreationCard(
    feature: HomeFeature,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        color = WhiteColor,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(feature.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = stringResource(feature.titleRes),
                fontFamily = SfProDisplayBold,
                fontSize = 16.sp,
                color = TextColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = TextColor,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(12.dp)
            )
        }
    }
}

@Composable
private fun FeatureGrid(
    features: List<HomeFeature>,
    onFeatureClick: (HomeFeature) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        features.chunked(2).forEach { rowFeatures ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                rowFeatures.forEach { feature ->
                    Box(modifier = Modifier.weight(1f)) {
                        FeatureItem(
                            feature = feature,
                            onClick = {
                                onFeatureClick(feature)
                            }
                        )
                    }
                }
                if (rowFeatures.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    feature: HomeFeature,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(feature.imageRes),
            contentDescription = stringResource(feature.titleRes),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.12f)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = stringResource(feature.titleRes),
            fontFamily = SfProDisplayBold,
            fontSize = 14.sp,
            color = TextColor,
            textAlign = TextAlign.Center
        )
    }
}
