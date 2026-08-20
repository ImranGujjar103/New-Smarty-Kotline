package com.imr.example.newsmartykotlin.presentation.gallery

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.permission.AppPermissionType
import com.imr.example.newsmartykotlin.domain.model.GalleryImage
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.navigation.SELECTED_BACKGROUND_IMAGE_KEY
import com.imr.example.newsmartykotlin.presentation.permission.AppSettingsHelper
import com.imr.example.newsmartykotlin.presentation.permission.GalleryPermissionHelper
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.AppTypography
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.RedColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SubTextColor
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryScreen(
    navController: NavController,
    isForBackground: Boolean = false,
    viewModel: GalleryViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()
    val storageDeniedCount by adViewModel.dataStorePrefs.getPermissionDeniedCount(AppPermissionType.STORAGE).collectAsStateWithLifecycle(initialValue = 0)

    val showAd = config.galleryNative.toShow && !isPurchased && isConnected && !uiState.isForBgRemover

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.loadGalleryImages()
        }
        viewModel.updatePermissionStatus(GalleryPermissionHelper.isLimitedAccess(context))
    }

    val onStorageAllowClick = {
        if (storageDeniedCount >= 2) {
            AppSettingsHelper.openAppSettings(context)
        } else {
            permissionLauncher.launch(GalleryPermissionHelper.getStoragePermissionLauncherPermissions())
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updatePermissionStatus(GalleryPermissionHelper.isLimitedAccess(context))
                viewModel.loadGalleryImages()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(config.galleryInterstitial.toShow) {
        if (config.galleryInterstitial.toShow && !isPurchased && isConnected && !uiState.isForBgRemover) {
            adViewModel.loadInterstitialAd(
                adId = config.galleryInterstitial.adId,
                tag = "Gallery_Interstitial"
            )
        }
    }

    val nativeState by adViewModel.getNativeAdState("GalleryNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.galleryNative.adId,
                tag = "GalleryNative"
            ) { _ -> }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                GalleryEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is GalleryEvent.NavigateToCropFace -> {
                    navController.navigate(
                        AppRoutes.CropFace.createRoute(
                            suitUrl = event.suitUrl,
                            imageUri = event.imageUri
                        )
                    )
                }

                is GalleryEvent.NavigateToBgRemoverCrop -> {
                    navController.navigate(
                        AppRoutes.CropForBgRemover.createRoute(
                            imageUri = event.imageUri
                        )
                    )
                }

                is GalleryEvent.NavigateToPassportCrop -> {
                    navController.navigate(
                        AppRoutes.PassportCropper.createRoute(
                            imageUri = event.imageUri,
                            countryId = event.countryId,
                            documentType = event.documentType
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadGalleryImages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            GalleryTopBar(
                onBackClick = viewModel::onBackClick
            )
            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = WhiteColor,
                shape = RoundedCornerShape(
                    topStart = 22.dp,
                    topEnd = 22.dp
                )
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryColor
                            )
                        }
                    }

                    uiState.errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.errorMessage ?: stringResource(R.string.something_went_wrong),
                                style = AppTypography.Body,
                                color = RedColor
                            )
                        }
                    }

                    uiState.filteredImages.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            FolderTabsRow(
                                folders = uiState.folders,
                                selectedFolder = uiState.selectedFolderName,
                                onFolderClick = viewModel::onFolderClick
                            )

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_images_found),
                                    style = AppTypography.Body,
                                    color = SubTextColor
                                )
                            }
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {


                            if (uiState.isLimitedAccess) {
                                LimitedAccessCard(onAllowClick = onStorageAllowClick)
                            }

                            FolderTabsRow(
                                folders = uiState.folders,
                                selectedFolder = uiState.selectedFolderName,
                                onFolderClick = viewModel::onFolderClick
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 22.dp),
                                contentPadding = PaddingValues(
                                    top = 14.dp,
                                    bottom = 20.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.filteredImages,
                                    key = { it.id }
                                ) { image ->
                                    GalleryImageItem(
                                        image = image,
                                        onImageClick = { selectedImage ->
                                            if (isForBackground) {
                                                navController.previousBackStackEntry
                                                    ?.savedStateHandle
                                                    ?.set(
                                                        SELECTED_BACKGROUND_IMAGE_KEY,
                                                        selectedImage.uri
                                                    )

                                                navController.popBackStack()
                                            } else {
                                                adViewModel.showInterstitialAd(
                                                    activity = navController.context as ComponentActivity,
                                                    toShow = config.galleryInterstitial.toShow,
                                                    adId = config.galleryInterstitial.adId,
                                                    tag = "Gallery_Interstitial",
                                                    callback = {
                                                        viewModel.onImageClick(selectedImage)
                                                    }
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAd) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                LanguageBottomNativeAd(
                    state = nativeState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun LimitedAccessCard(onAllowClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Allow access to more media\nfor a better experience.",
                fontSize = 13.sp,
                fontFamily = SfProDisplayBold,
                color = TextColor,
                lineHeight = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = onAllowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = WhiteColor
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Allow More",
                    fontSize = 13.sp,
                    fontFamily = SfProDisplayBold,
                    color = WhiteColor
                )
            }
        }
    }
}

@Composable
private fun FolderTabsRow(
    folders: List<String>,
    selectedFolder: String,
    onFolderClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .background(WhiteColor)
            .padding(
                horizontal = 10.dp,
                vertical = 10.dp
            )
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        folders.forEach { folder ->
            val selected = folder == selectedFolder

            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) {
                            PrimaryColor
                        } else {
                            CardColor
                        }
                    )
                    .clickable {
                        onFolderClick(folder)
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = folder,
                    fontFamily = SfProDisplayBold,
                    color = if (selected) WhiteColor else TextColor,
                    maxLines = 1,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GalleryTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(
                HomeBackgroundColor
            )
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable(true, onClick = onBackClick)
            ,
            contentAlignment = Alignment.Center
        ) {
            Icon(modifier = Modifier.size(10.dp),
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                tint = WhiteColor
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = stringResource(R.string.select_image),
            fontSize = 18.sp,
            fontFamily = SfProDisplayBold,
            color = TextColor
        )
    }
}

@Composable
private fun GalleryImageItem(
    image: GalleryImage,
    onImageClick: (GalleryImage) -> Unit
) {
    AsyncImage(
        model = image.uri.toUri(),
        contentDescription = image.fileName,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onImageClick(image)
            },
        contentScale = ContentScale.Crop
    )
}
