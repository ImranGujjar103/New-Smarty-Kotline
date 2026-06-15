package com.imr.example.newsmartykotlin.presentation.passport.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.TextColor

@Composable
fun PassportInfoRow(
    titleRes: Int,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(titleRes),
            fontFamily = SfProDisplayRegular,
            fontSize = 12.sp,
            color = TextColor
        )

        Text(
            text = value,
            fontFamily = SfProDisplayRegular,
            fontSize = 12.sp,
            color = TextColor
        )
    }
}