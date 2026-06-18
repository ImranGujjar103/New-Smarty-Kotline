package com.imr.example.newsmartykotlin.presentation.premium

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.DisabledTextColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun PremiumTwoCardScreen(
    state: PremiumUiState,
    onCloseClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onContinueClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier
                .padding(top = 20.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(DisabledTextColor)
                .clickable { onCloseClick() }
                .padding(12.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(30.dp))

            Image(
                painter = painterResource(R.drawable.ic_premium_crown),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(Modifier.height(20.dp))

            PremiumTitle()
            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.premium_subtitle),
                fontFamily = SfProDisplayBold,
                fontSize = 13.sp,
                color = TextColor,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(30.dp))

            PremiumFeatureTable()

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.plans.take(2).forEach { plan ->
                    PremiumPlanCard(
                        modifier = Modifier.weight(1f),
                        title = plan.title,
                        price = plan.price,
                        selected = plan.isSelected,
                        onClick = { onPlanClick(plan.productId) }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.premium_trial_text),
                fontFamily = SfProDisplayBold,
                fontSize = 10.sp,
                color = TextColor,
                lineHeight = 7.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            PremiumContinueButton(
                enabled = !state.isLoading,
                onClick = onContinueClick
            )

            Spacer(Modifier.height(10.dp))

            PremiumBottomText()
        }

        PremiumLinks(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
        )
    }
}

@Composable
private fun PremiumPlanCard(
    modifier: Modifier,
    title: String,
    price: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(WhiteColor)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(0.68f)
                .clip(RoundedCornerShape(bottomEnd = 20.dp))
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontFamily = SfProDisplayBold,
                fontSize = 18.sp,
                color = WhiteColor
            )
        }

        Text(
            text = price.ifEmpty { stringResource(R.string.loading) },
            fontFamily = SfProDisplayBold,
            fontSize = 20.sp,
            color = TextColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}