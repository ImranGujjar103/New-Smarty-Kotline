package com.imr.example.newsmartykotlin.presentation.passport.result

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toUri
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.navigation.RESULT_UPDATED_IMAGE_URI
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
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
    viewModel: PassportResultViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.passportResultNative.toShow && !isPurchased && isConnected

    LaunchedEffect(config.saveInterstitial.toShow) {
        if (config.saveInterstitial.toShow && !isPurchased && isConnected) {
            adViewModel.loadInterstitialAd(
                adId = config.saveInterstitial.adId,
                tag = "Save_Interstitial"
            )
        }
    }

    val nativeState by adViewModel.getNativeAdState("PassportResultNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.passportResultNative.adId,
                tag = "PassportResultNative"
            ) { _ -> }
        }
    }

    val updatedImageUri = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(RESULT_UPDATED_IMAGE_URI, "")
        ?.collectAsState()

    LaunchedEffect(updatedImageUri?.value) {
        val uri = updatedImageUri?.value.orEmpty()

        if (uri.isNotBlank()) {
            viewModel.onBackgroundImageUpdated(uri)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>(RESULT_UPDATED_IMAGE_URI)
        }
    }

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
                        AppRoutes.Background.createRoute(event.imageUri)
                    )
                }

                is PassportResultEvent.ImageSaved -> {
                    adViewModel.showInterstitialAd(
                        activity = navController.context as ComponentActivity,
                        toShow = config.saveInterstitial.toShow,
                        adId = config.saveInterstitial.adId,
                        tag = "Save_Interstitial",
                        callback = {
                            navController.navigate(
                                AppRoutes.Saved.createRoute(
                                    imagePath = event.uri,
                                    isForPassport = true,
                                    countryId = uiState.countryId,
                                    documentType = uiState.documentType
                                )
                            ) {
                                popUpTo(AppRoutes.PassportResult.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                    Toast.makeText(
                        navController.context,
                        navController.context.getString(
                            R.string.image_saved_successfully
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
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
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp).padding(top = 25.dp)
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
                    title = stringResource(R.string.try_again),
                    icon = R.drawable.ic_refresh,
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::onTryAgainClick
                )

                PassportResultSmallButton(
                    title = stringResource(R.string.background),
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
                    text = stringResource(R.string.save),
                    color = WhiteColor,
                    fontSize = 16.sp,
                    fontFamily = SfProDisplayBold
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showAd) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                LanguageBottomNativeAd(
                    state = nativeState,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
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
            text = stringResource(R.string.select_document_type),
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
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center){
        Box(
            modifier = Modifier
                .height(208.dp).width(249.dp),
            contentAlignment = Alignment.Center
        )
        {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri.toUri())
                        .allowHardware(false)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            // Top horizontal line
            Box(

                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = "${pixelText.substringBefore(" x").trim()} ${stringResource(R.string.inch)}",
                    fontSize = 9.sp,
                    color = TextColor,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .width(150.dp)
                        .height(1.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )
            }

            // Bottom horizontal line
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(150.dp)
                        .height(1.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )

                Text(
                    text = "${inchText.substringBefore(" x").trim()} ${stringResource(R.string.inch)}",
                    fontSize = 8.sp,
                    color = TextColor,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            // Left vertical line
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)

            ) {
                Text(
                    text = pixelText.substringAfter("x").trim(),
                    fontSize = 9.sp,
                    color = TextColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd).padding(end = 6.dp)

                )

                Box(
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .width(1.dp)
                        .height(150.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )
            }

            // Right vertical line
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(150.dp)
                        .background(TextColor.copy(alpha = 0.6f))
                )

                Text(
                    text = inchText.substringAfter("x").trim(),
                    fontSize = 9.sp,
                    color = TextColor,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }
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
            title = stringResource(R.string.printable),
            value = dpiText
        )

        PassportResultInfoRow(
            title = stringResource(R.string.suitable_for_online_submission),
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
