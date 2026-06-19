package com.imr.example.newsmartykotlin.presentation.passport

import PassportTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.model.getPixel
import com.imr.example.newsmartykotlin.domain.model.getSizeInch
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportActionButton
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportInfoRow
import com.imr.example.newsmartykotlin.presentation.passport.components.PassportPreviewBox
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
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
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(25.dp))
        PassportTopBar(
            title = stringResource(R.string.select_document_type),
            onBackClick = onBackClick
        )
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