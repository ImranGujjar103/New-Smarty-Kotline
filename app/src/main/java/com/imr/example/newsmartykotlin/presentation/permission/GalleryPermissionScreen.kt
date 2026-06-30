package com.imr.example.newsmartykotlin.presentation.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayMedium
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryPermissionScreen(
    navController: NavController,
    viewModel: GalleryPermissionViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsState()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.galleryPermissionNative.toShow && !isPurchased && isConnected

    var nativeState by remember { mutableStateOf<LanguageNativeState>(LanguageNativeState.Idle) }

    LaunchedEffect(showAd) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            nativeState = LanguageNativeState.Loading
            adViewModel.loadNativeAd(
                adId = config.galleryPermissionNative.adId,
                tag = "GalleryPermissionBottomNative"
            ) { ad ->
                nativeState = if (ad != null) {
                    LanguageNativeState.Loaded(ad)
                } else {
                    LanguageNativeState.Failed
                }
            }
        }
    }

    val isAlreadyGranted = remember(context) {
        GalleryPermissionHelper.hasGalleryPermission(context)
    }

    val permission = GalleryPermissionHelper.getRequiredPermission()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted || GalleryPermissionHelper.hasGalleryPermission(context)) {
            viewModel.onPermissionGranted()
        } else {
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permission
                )
            } ?: false

            if (!shouldShowRationale && uiState.denyCount > 1) {
                viewModel.showSettingsDialog()
            } else {
                viewModel.onPermissionDenied()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (isAlreadyGranted) {
            viewModel.onPermissionGranted()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionOnResume(
                    GalleryPermissionHelper.hasGalleryPermission(context)
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                GalleryPermissionEvent.RequestPermission -> {
                    val shouldShowRationale = activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            permission
                        )
                    } ?: false

                    val hasPermission = GalleryPermissionHelper.hasGalleryPermission(context)

                    if (hasPermission) {
                        viewModel.onPermissionGranted()
                    } else if (!shouldShowRationale && uiState.denyCount > 1) {
                        viewModel.showSettingsDialog()
                    } else {
                        permissionLauncher.launch(permission)
                    }
                }

                GalleryPermissionEvent.OpenSettings -> {
                    AppSettingsHelper.openAppSettings(context)
                }

                GalleryPermissionEvent.NavigateGallery -> {
                    navController.navigate(
                        AppRoutes.Gallery.createRoute(
                            suitUrl = viewModel.suitId
                        )
                    ) {
                        popUpTo(AppRoutes.GalleryPermission.route) {
                            inclusive = true
                        }
                    }
                }

                GalleryPermissionEvent.NavigateBgRemoverGallery -> {
                    navController.navigate(
                        AppRoutes.GalleryForBgRemover.createRoute()
                    ) {
                        popUpTo(AppRoutes.GalleryPermissionForBgRemover.route) {
                            inclusive = true
                        }
                    }
                }

                GalleryPermissionEvent.NavigateBackgroundGallery -> {
                    navController.navigate(
                        AppRoutes.GalleryForBackground.route
                    ) {
                        popUpTo(AppRoutes.GalleryPermissionForBackground.route) {
                            inclusive = true
                        }
                    }
                }

                is GalleryPermissionEvent.NavigatePassportGallery -> {
                    navController.navigate(
                        AppRoutes.GalleryForPassport.createRoute(
                            countryId = event.countryId,
                            documentType = event.documentType
                        )
                    ) {
                        popUpTo(AppRoutes.GalleryPermissionForPassport.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    if (isAlreadyGranted || uiState.isPermissionGranted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBackgroundColor)
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(25.dp))

                GalleryPermissionTopBar(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = WhiteColor,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gallery_empty),
                            contentDescription = null,
                            modifier = Modifier.size(122.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(R.string.no_images_to_show),
                            fontFamily = SfProDisplayBold,
                            fontSize = 16.sp,
                            color = TextColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = stringResource(R.string.permission_required_to_access_your_photo_gallery),
                            fontFamily = SfProDisplayMedium,
                            color = TextColor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        GradientPermissionButton(
                            text = stringResource(R.string.grant_permission),
                            onClick = viewModel::onGrantPermissionClick
                        )
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

        if (uiState.showUnlockDialog) {
            UnlockLooksDialog(
                onCancelClick = viewModel::onUnlockDialogCancel,
                onAllowClick = viewModel::onUnlockDialogAllow
            )
        }

        if (uiState.showSettingsDialog) {
            PermissionSettingsDialog(
                onCancelClick = viewModel::onSettingsDialogCancel,
                onSettingsClick = viewModel::onSettingsClick
            )
        }
    }
}

@Composable
private fun GalleryPermissionTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(
                HomeBackgroundColor
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth(),
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

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(R.string.select_image),
                fontSize = 18.sp,
                fontFamily = SfProDisplayBold,
                color = TextColor
            )
        }
    }
}

@Composable
private fun GradientPermissionButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.58f)
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                PrimaryColor
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = WhiteColor,
            fontSize = 16.sp,
            fontFamily = SfProDisplayBold
        )
    }
}

private fun Context.findActivity(): Activity? {
    var currentContext = this

    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }

        currentContext = currentContext.baseContext
    }

    return null
}
