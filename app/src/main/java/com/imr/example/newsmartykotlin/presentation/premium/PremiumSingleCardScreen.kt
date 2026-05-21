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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.*

@Composable
fun PremiumSingleCardScreen(
    state: PremiumUiState,
    onCloseClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onContinueClick: () -> Unit
) {
    val plan = state.plans.firstOrNull()

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

            Spacer(Modifier.height(32.dp))

            PremiumFeatureTable()

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(74.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(WhiteColor)
                    .clickable {
                        plan?.let { onPlanClick(it.productId) }
                    }
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan?.title ?: stringResource(R.string.monthly),
                        fontFamily = SfProDisplayBold,
                        fontSize = 18.sp,
                        color = TextColor
                    )

                    Text(
                        text = plan?.price ?: stringResource(R.string.loading),
                        fontFamily = SfProDisplayBold,
                        fontSize = 18.sp,
                        color = TextColor
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

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