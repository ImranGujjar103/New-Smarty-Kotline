package com.imr.example.newsmartykotlin.presentation.passport.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayMedium
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun PassportResultScreen(
    navController: NavController,
    viewModel: PassportResultViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                PassportResultEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                PassportResultEvent.TryAgain -> {
                    navController.navigate(
                        AppRoutes.PassportDetail.createRoute(
                            countryId = uiState.countryId,
                            documentType = uiState.documentType
                        )
                    ) {
                        popUpTo(AppRoutes.PassportResult.route) {
                            inclusive = true
                        }
                    }
                }

                is PassportResultEvent.NavigateToBackground -> {
                    navController.navigate(
                        AppRoutes.BackgroundText.createRoute(event.imageUri)
                    )
                }

                is PassportResultEvent.SaveImage -> {
                    // connect your existing save/download use case here
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp)
    ) {
        PassportResultTopBar(
            onBackClick = viewModel::onBackClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        PassportResultPreview(
            imageUri = uiState.imageUri,
            pixelText = uiState.pixelText,
            inchText = uiState.inchText
        )

        Spacer(modifier = Modifier.height(28.dp))

        PassportResultInfoCard(
            dpiText = uiState.dpiText,
            fileSizeText = uiState.fileSizeText.ifBlank { "234 KB" }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PassportResultSmallButton(
                title = "Try again",
                icon = R.drawable.ic_refresh,
                modifier = Modifier.weight(1f),
                onClick = viewModel::onTryAgainClick
            )

            PassportResultSmallButton(
                title = "Background",
                icon = R.drawable.ic_bg_remove,
                modifier = Modifier.weight(1f),
                onClick = viewModel::onBackgroundClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PrimaryColor)
                .clickable { viewModel.onSaveClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Save",
                color = WhiteColor,
                fontSize = 16.sp,
                fontFamily = SfProDisplayBold
            )
        }
    }
}

@Composable
private fun PassportResultTopBar(
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                tint = WhiteColor,
                modifier = Modifier.size(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Select document type",
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp,
            color = TextColor
        )
    }
}

@Composable
private fun PassportResultPreview(
    imageUri: String,
    pixelText: String,
    inchText: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = pixelText.ifBlank { "600 px" },
            fontSize = 7.sp,
            fontFamily = SfProDisplayMedium,
            color = TextColor
        )

        Box(
            modifier = Modifier
                .width(190.dp)
                .height(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri.toUri()),
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = inchText.ifBlank { "2.0 inch" },
            fontSize = 7.sp,
            fontFamily = SfProDisplayMedium,
            color = TextColor
        )
    }
}

@Composable
private fun PassportResultInfoCard(
    dpiText: String,
    fileSizeText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WhiteColor)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PassportResultInfoRow(
            title = "Printable",
            value = dpiText
        )

        PassportResultInfoRow(
            title = "Suitable for online submission",
            value = fileSizeText
        )
    }
}

@Composable
private fun PassportResultInfoRow(
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_check_circle),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            fontSize = 13.sp,
            fontFamily = SfProDisplayMedium,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontFamily = SfProDisplayMedium,
            color = TextColor
        )
    }
}

@Composable
private fun PassportResultSmallButton(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(WhiteColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontFamily = SfProDisplayBold,
                color = TextColor
            )
        }
    }
}