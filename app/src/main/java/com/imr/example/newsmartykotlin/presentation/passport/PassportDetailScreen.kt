package com.imr.example.newsmartykotlin.presentation.passport

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.model.getPixel
import com.imr.example.newsmartykotlin.domain.model.getSizeInch
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportActionButton
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportInfoRow
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportPreviewBox
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun PassportDetailScreen(
    country: PassportCountry,
    selectedType: DocumentType,
    finalImageUri: String?,
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp).padding(top = 30.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = PrimaryColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    tint = WhiteColor,
                    modifier = Modifier.size(10.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                text = stringResource(R.string.select_document_type),
                fontFamily = SfProDisplayBold,
                fontSize = 18.sp,
                color = TextColor
            )
        }

        Spacer(Modifier.height(36.dp))

        PassportPreviewBox(
            imageRes = R.drawable.ic_passport_sample,
            imageUri = finalImageUri,
            pixelText = country.getPixel(selectedType),
            inchText = country.getSizeInch(selectedType)
        )

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhiteColor, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PassportInfoRow(R.string.size, country.getSizeInch(selectedType))
            PassportInfoRow(R.string.pixel, country.getPixel(selectedType))
            PassportInfoRow(R.string.background, country.background)
        }

        Spacer(Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            PassportActionButton(
                title = stringResource(R.string.camera),
                icon = R.drawable.ic_camera,
                onClick = onCameraClick,
                modifier = Modifier.weight(1f)
            )

            PassportActionButton(
                title = stringResource(R.string.gallery),
                icon = R.drawable.ic_gallery_passport,
                onClick = onGalleryClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}