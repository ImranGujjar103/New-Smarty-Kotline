package com.imr.example.newsmartykotlin.presentation.language.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.LanguageModel
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor


@Composable
fun LanguageItem(
    language: LanguageModel,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (language.isSelected) {
            PrimaryColor
        } else {
            WhiteColor
        },
        label = "languageBackground"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp)) // Move before clickable
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = language.flags),
            contentDescription = language.languageName,
            modifier = Modifier.size(30.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = language.languageName,
            fontFamily = SfProDisplayBold,
            fontSize = 14.sp,
            color = if (language.isSelected) {
                WhiteColor
            } else {
                TextColor
            },
            modifier = Modifier.weight(1f)
        )

        if (language.isSelected) {
            Icon(
                painter =painterResource(R.drawable.ic_language_tick),
                contentDescription = null,
                tint = WhiteColor,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun HandTutorialAnimation(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "hand")

    val offsetX by transition.animateFloat(
        initialValue = 0f,
        targetValue = -18f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handOffset"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handAlpha"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_hand_tap),
        contentDescription = null,
        modifier = modifier
            .size(64.dp)
            .offset(x = offsetX.dp)
            .alpha(alpha)
    )
}