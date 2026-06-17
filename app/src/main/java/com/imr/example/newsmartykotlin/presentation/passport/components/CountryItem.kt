package com.imr.example.newsmartykotlin.presentation.passport.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.model.getSizeMm
import com.imr.example.newsmartykotlin.ui.theme.DisabledTextColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun PassportCountryItem(
    country: PassportCountry,
    selectedType: DocumentType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(WhiteColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(country.flagRes),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(country.nameRes),
                fontFamily = SfProDisplayBold,
                fontSize = 16.sp,
                color = TextColor
            )

            Text(
                text = country.getSizeMm(selectedType),
                fontFamily = SfProDisplayRegular,
                fontSize = 13.sp,
                color = DisabledTextColor
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = TextColor
        )
    }
}