package com.imr.example.newsmartykotlin.presentation.passport

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.utils.CacheImageFileManager
import com.imr.example.newsmartykotlin.domain.model.DocumentType
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.permission.AppSettingsHelper
import com.imr.example.newsmartykotlin.presentation.permission.CameraPermissionDialog
import com.imr.example.newsmartykotlin.presentation.permission.CameraPermissionHelper
import com.imr.example.newsmartykotlin.presentation.permission.PermissionSettingsDialog
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PassportDetailRoute(
    countryId: String,
    selectedType: DocumentType,
    onBackClick: () -> Unit,
    onCameraImageCaptured: (String) -> Unit,
    onGalleryClick: () -> Unit,
    viewModel: PassportDetailViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel(),
    cacheImageFileManager: CacheImageFileManager = koinInject()
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.passportDetailNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("PassportDetailNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.passportDetailNative.adId,
                tag = "PassportDetailNative"
            ) { _ -> }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionOnResume(
                    CameraPermissionHelper.hasCameraPermission(context)
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val country = remember(countryId) {
        viewModel.getCountry(countryId)
    }

    var cameraUri: Uri? = remember { null }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.toString()?.let { uri ->
                onCameraImageCaptured(uri)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
            cameraUri = cacheImageFileManager.createCameraImageUri()
            cameraUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        } else {
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.CAMERA
                )
            } ?: false

            if (!shouldShowRationale && uiState.cameraDenyCount > 0) {
                viewModel.showSettingsDialog()
            } else {
                viewModel.onPermissionDenied()
            }
        }
    }

    country?.let {
        PassportDetailScreen(
            country = it,
            selectedType = selectedType,
            finalImageUri = uiState.finalImageUri,
            nativeState = nativeState,
            showAd = showAd,
            onBackClick = onBackClick,
            onCameraClick = {
                if (CameraPermissionHelper.hasCameraPermission(context)) {
                    cameraUri = cacheImageFileManager.createCameraImageUri()
                    cameraUri?.let { uri ->
                        cameraLauncher.launch(uri)
                    }
                } else {
                    viewModel.onCameraClick()
                }
            },
            onGalleryClick = onGalleryClick
        )
    }

    if (uiState.showPermissionDialog) {
        CameraPermissionDialog(
            onCancelClick = viewModel::onPermissionDialogDeny,
            onAllowClick = {
                viewModel.onPermissionDialogAllow()
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }

    if (uiState.showSettingsDialog) {
        PermissionSettingsDialog(
            message = stringResource(R.string.camera_access_is_required_to_continue_please_enable_it_from_settings),
            onCancelClick = viewModel::onSettingsDialogCancel,
            onSettingsClick = {
                viewModel.onSettingsClick()
                AppSettingsHelper.openAppSettings(context)
            }
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
