package com.imr.example.newsmartykotlin.presentation.premium

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.*

@Composable
fun PremiumTitle() {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = SubTextColor)) {
                append(stringResource(R.string.get_text))
                append(" ")
            }
            withStyle(
                SpanStyle(
                    color = WhiteColor,
                    background = Orang2Color
                )
            ) {
                append("  ")
                append(stringResource(R.string.premium))
                append("  ")
            }
        },
        fontFamily = SfProDisplayBold,
        fontSize = 28.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun PremiumFeatureTable() {
    Column(modifier = Modifier.fillMaxWidth()) {
        FeatureRow(
            feature = stringResource(R.string.features),
            free = stringResource(R.string.free),
            pro = stringResource(R.string.pro),
            isHeader = true
        )

        FeatureRow(stringResource(R.string.unlimited_photo_editing), true, true)
        FeatureRow(stringResource(R.string.unlimited_ai_suit_changer), false, true)
        FeatureRow(stringResource(R.string.unlimited_bg_removes), false, true)
        FeatureRow(stringResource(R.string.remove_all_ads), false, true)
    }
}

@Composable
private fun FeatureRow(
    feature: String,
    free: String,
    pro: String,
    isHeader: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            fontFamily = SfProDisplayBold,
            fontSize = if (isHeader) 16.sp else 15.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = free,
            fontFamily = SfProDisplayBold,
            fontSize = 15.sp,
            color = TextColor,
            modifier = Modifier.width(54.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = pro,
            fontFamily = SfProDisplayBold,
            fontSize = 15.sp,
            color = TextColor,
            modifier = Modifier.width(54.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FeatureRow(
    feature: String,
    freeAvailable: Boolean,
    proAvailable: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            fontFamily = SfProDisplayRegular,
            fontSize = 15.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        FeatureStatus(
            available = freeAvailable,
            modifier = Modifier.width(54.dp)
        )

        FeatureStatus(
            available = proAvailable,
            modifier = Modifier.width(54.dp)
        )
    }
}

@Composable
private fun FeatureStatus(
    available: Boolean,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (available) {
            Icon(
                painter = painterResource(R.drawable.ic_premium_tick),
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = "-",
                fontFamily = SfProDisplayBold,
                fontSize = 18.sp,
                color = DisabledTextColor
            )
        }
    }
}

@Composable
fun PremiumContinueButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            disabledContainerColor = DisabledTextColor,
            contentColor = WhiteColor,
            disabledContentColor = WhiteColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Text(
            text = stringResource(R.string.continue_for_free),
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun PremiumBottomText() {
    Text(
        text = stringResource(R.string.subscription_auto_renew_text),
        fontFamily = SfProDisplayRegular,
        fontSize = 10.sp,
        color = TextColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 18.dp)
    )

    Spacer(Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.no_commitment_cancel_anytime),
        fontFamily = SfProDisplayRegular,
        fontSize = 11.sp,
        color = TextColor,
        textAlign = TextAlign.Center
    )
}

@Composable
fun PremiumLinks(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PremiumLinkText(stringResource(R.string.terms_and_conditions))
        PremiumLinkText(stringResource(R.string.privacy_policy))
        PremiumLinkText(stringResource(R.string.subscription_details))
    }
}

@Composable
private fun PremiumLinkText(text: String) {
    Text(
        text = text,
        fontFamily = SfProDisplayBold,
        fontSize = 10.sp,
        color = PrimaryColor
    )
}