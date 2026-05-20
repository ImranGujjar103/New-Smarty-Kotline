package com.imr.example.newsmartykotlin.presentation.language

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.LanguageModel
import com.imr.example.newsmartykotlin.presentation.language.components.HandTutorialAnimation
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageItem
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageTopBannerAd
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.DisabledTextColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun LanguageScreen(
    state: LanguageUiState,
    bannerState: LanguageBannerState,
    nativeState: LanguageNativeState,
    onLanguageClick: (LanguageModel) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        LanguageTopBannerAd(
            state = bannerState,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.select_language),
                    fontFamily = SfProDisplayBold,
                    color = TextColor,
                    fontSize = 18.sp
                )

                Text(
                    text = stringResource(R.string.select_language_subtitle),
                    fontFamily = SfProDisplayRegular,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = TextColor
                )
            }

            Button(
                modifier = Modifier
                    .width(80.dp).height(40.dp)
                    .clip(RoundedCornerShape(14.dp)),
                onClick = onSaveClick,
                enabled = state.isSaveEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = CardColor,
                    contentColor = WhiteColor,
                    disabledContentColor = DisabledTextColor
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontFamily = SfProDisplayBold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = state.languages,
                    key = { it.languageCode }
                ) { language ->
                    LanguageItem(
                        language = language,
                        onClick = { onLanguageClick(language) }
                    )
                }
            }

            if (state.showHandAnimation) {
                HandTutorialAnimation(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 42.dp, end = 34.dp)
                )
            }
        }

        LanguageBottomNativeAd(
            state = nativeState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}