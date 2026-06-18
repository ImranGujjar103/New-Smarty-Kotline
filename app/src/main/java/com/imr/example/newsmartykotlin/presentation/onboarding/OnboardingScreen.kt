package com.imr.example.newsmartykotlin.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    nativeState: LanguageNativeState,
    onPageChanged: (Int) -> Unit,
    onNextClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { uiState.pages.size }
    )

    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColor)
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(
                page = uiState.pages[page],
                currentPage = uiState.currentPage,
                totalPages = uiState.pages.size,
                onNextClick = onNextClick
            )
        }

        LanguageBottomNativeAd(
            state = nativeState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    currentPage: Int,
    totalPages: Int,
    onNextClick: () -> Unit

) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .fillMaxHeight(0.8f),

            contentScale = ContentScale.Crop,


        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CardColor.copy(0.5f),
                            CardColor,
                            CardColor,
                            CardColor
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.2f)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(page.titleRes),
                fontFamily = SfProDisplayBold,
                fontSize = 20.sp,
                color = TextColor
            )
            if (page.descRes != 0){
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(page.descRes),
                    fontFamily = SfProDisplayBold,
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    color = TextColor
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PageIndicator(
                    currentPage = currentPage,
                    totalPages = totalPages
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onNextClick,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp).width(60.dp),
                    ) {
                    Text(
                        modifier = Modifier.wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.next),
                        fontFamily = SfProDisplayBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(if (index == currentPage) 20.dp else 10.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (index == currentPage) PrimaryColor
                        else WhiteColor
                    )
            )
        }
    }
}
