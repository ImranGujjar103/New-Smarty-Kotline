package com.imr.example.newsmartykotlin.presentation.premium

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.*

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
            .padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier
                .padding(top = 62.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(DisabledTextColor)
                .clickable { onCloseClick() }
                .padding(10.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(120.dp))

            Image(
                painter = painterResource(R.drawable.ic_premium_crown),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )

            Spacer(Modifier.height(20.dp))

            PremiumTitle()

            Text(
                text = stringResource(R.string.premium_subtitle),
                fontFamily = SfProDisplayBold,
                fontSize = 13.sp,
                color = TextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(26.dp))

            PremiumFeatureTable()

            Spacer(Modifier.height(22.dp))

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
                .padding(bottom = 22.dp)
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
            .height(106.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(WhiteColor)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .fillMaxWidth(0.68f)
                .clip(RoundedCornerShape(bottomEnd = 18.dp))
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
            fontSize = 22.sp,
            color = TextColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}