package com.imr.example.newsmartykotlin.presentation.premium

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.utils.BillingManager
import com.imr.example.newsmartykotlin.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import com.imr.example.newsmartykotlin.domain.model.PremiumPlan

@Composable
fun PremiumSingleCardScreen(
    state: PremiumUiState,
    onCloseClick: () -> Unit,
    onPlanClick: (String) -> Unit,
    onContinueClick: () -> Unit,
    onPurchaseClick: (String) -> Unit
) {
    val weeklyPlan = state.plans.find { it.productId == BillingManager.WEEKLY_SUBSCRIPTION_ID }
    val monthlyPlan = state.plans.find { it.productId == BillingManager.MONTHLY_SUBSCRIPTION_ID }

    LaunchedEffect(state) {
        Log.d("PremiumSingleCard", "UI State updated: isLoading=${state.isLoading}, plans count=${state.plans.size}")
        state.plans.forEach { plan ->
            Log.d("PremiumSingleCard", "Plan: ${plan.productId}, price=${plan.price}, hasTrial=${plan.hasTrial}")
        }
        Log.d("PremiumSingleCard", "Weekly plan found: ${weeklyPlan != null}, Monthly plan found: ${monthlyPlan != null}")
    }

    val isReady = weeklyPlan != null && weeklyPlan.price != "Loading..." &&
            monthlyPlan != null && monthlyPlan.price != "Loading..."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(CardColor)
            .padding(horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier
                .padding(top = 30.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(DisabledTextColor)
                .clickable { onCloseClick() }
                .padding(10.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

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
                fontSize = 12.sp,
                lineHeight = 11.sp,
                color = TextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(30.dp))

            PremiumFeatureTable()

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(WhiteColor)
                    .clickable(enabled = isReady) {
                        monthlyPlan?.let { onPurchaseClick(it.productId) }
                    }
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthlyPlan?.title ?: stringResource(R.string.monthly),
                        fontFamily = SfProDisplayBold,
                        fontSize = 18.sp,
                        color = TextColor
                    )

                    Text(
                        text = if (monthlyPlan != null && monthlyPlan.price != stringResource(R.string.loading)) {
                            "${monthlyPlan.price}/Month"
                        } else {
                            monthlyPlan?.price ?: stringResource(R.string.loading)
                        },
                        fontFamily = SfProDisplayBold,
                        fontSize = 16.sp,
                        color = TextColor
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            val weeklyTrialText = when {
                weeklyPlan == null || weeklyPlan.price == "Loading..." -> stringResource(R.string.premium_trial_text)
                weeklyPlan.hasTrial -> {
                    "${weeklyPlan.trialInfo} then ${weeklyPlan.trialInfoAfter.replace("Then ", "").replace("/week", "/Week")}"
                }
                else -> "${weeklyPlan.price}/Week"
            }

            Text(
                text = weeklyTrialText,
                fontFamily = SfProDisplayBold,
                fontSize = 10.sp,
                color = TextColor,
                textAlign = TextAlign.Center
            )

            PremiumContinueButton(
                hasTrial = weeklyPlan?.hasTrial ?: false,
                enabled = isReady,
                onClick = {
                    weeklyPlan?.let { onPurchaseClick(it.productId) }
                }
            )

            Spacer(Modifier.height(10.dp))

            PremiumBottomText()
        }

        PremiumLinks(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PremiumSingleCardNoTrialPreview() {
    NewSmartyKotlinTheme {
        PremiumSingleCardScreen(
            state = PremiumUiState(
                isLoading = false,
                plans = listOf(
                    PremiumPlan(
                        productId = BillingManager.WEEKLY_SUBSCRIPTION_ID,
                        title = "Weekly",
                        price = "$3.99",
                        billingPeriod = "/Week",
                        hasTrial = false
                    ),
                    PremiumPlan(
                        productId = BillingManager.MONTHLY_SUBSCRIPTION_ID,
                        title = "Monthly",
                        price = "$9.99",
                        billingPeriod = "/Month",
                        hasTrial = false
                    )
                )
            ),
            onCloseClick = {},
            onPlanClick = {},
            onContinueClick = {},
            onPurchaseClick = {}
        )
    }
}
