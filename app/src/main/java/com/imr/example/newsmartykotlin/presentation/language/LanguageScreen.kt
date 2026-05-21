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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.LanguageModel
import com.imr.example.newsmartykotlin.presentation.language.components.HandTutorialAnimation
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageItem
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
    nativeState: LanguageNativeState,
    onLanguageClick: (LanguageModel) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

/*        LanguageTopBannerAd(
            state = bannerState,
            modifier = Modifier.fillMaxWidth()
        )*/

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
                    fontSize = 24.sp
                )

                Text(
                    text = stringResource(R.string.select_language_subtitle),
                    fontFamily = SfProDisplayRegular,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = TextColor
                )
            }

            Button(
                modifier = Modifier
                    .width(60.dp).height(30.dp)
                    .clip(RoundedCornerShape(10.dp)),
                onClick = onSaveClick,
                enabled = state.isSaveEnabled,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = CardColor,
                    contentColor = WhiteColor,
                    disabledContentColor = DisabledTextColor
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.save),
                    fontFamily = SfProDisplayBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
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
                verticalArrangement = Arrangement.spacedBy(11.dp),
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