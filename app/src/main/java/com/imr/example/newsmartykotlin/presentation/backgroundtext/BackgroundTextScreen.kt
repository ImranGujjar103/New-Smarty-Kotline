package com.imr.example.newsmartykotlin.presentation.backgroundtext

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.extensions.clickableNoRipple
import com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet.BackgroundBottomSheet
import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.AddTextDialog
import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.EditableSticker
import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.StickerBottomSheet
import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.TextEditingBottomSheet
import android.app.Activity
import com.imr.example.newsmartykotlin.presentation.common.components.BannerAd
import com.imr.example.newsmartykotlin.presentation.common.components.BannerAdShimmer
import com.imr.example.newsmartykotlin.presentation.language.LanguageBannerState
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.navigation.SELECTED_BACKGROUND_IMAGE_KEY
import com.imr.example.newsmartykotlin.presentation.permission.GalleryPermissionHelper
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.SubTextColor
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundTextScreen(
    navController: NavController,
    viewModel: BackgroundTextViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showBannerAd = config.backgroundTextBanner.toShow && !isPurchased && isConnected
    val bannerState by adViewModel.getBannerAdState("BackgroundTextBanner").collectAsStateWithLifecycle()

    LaunchedEffect(showBannerAd) {
        if (showBannerAd) {
            adViewModel.loadBannerAd(
                activity = context as Activity,
                adId = config.backgroundTextBanner.adId,
                tag = "BackgroundTextBanner"
            )
        }
    }

    LaunchedEffect(config.saveInterstitial.toShow) {
        if (config.saveInterstitial.toShow && !isPurchased && isConnected) {
            adViewModel.loadInterstitialAd(
                adId = config.saveInterstitial.adId,
                tag = "Save_Interstitial"
            )
        }
    }

    val scope = rememberCoroutineScope()
    val captureLayer = rememberGraphicsLayer()
    var showBackgroundSheet by remember { mutableStateOf(false) }

    val selectedBackgroundImage =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<String?>(SELECTED_BACKGROUND_IMAGE_KEY, null)
            ?.collectAsState()


    LaunchedEffect(selectedBackgroundImage?.value) {
        selectedBackgroundImage?.value?.let { uri ->
            viewModel.updateBackground(uri)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>(SELECTED_BACKGROUND_IMAGE_KEY)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BackgroundTextEvent.Done -> {
                    adViewModel.showInterstitialAd(
                        activity = context as ComponentActivity,
                        toShow = config.saveInterstitial.toShow,
                        adId = config.saveInterstitial.adId,
                        tag = "Save_Interstitial",
                        callback = {
                            navController.navigate(
                                AppRoutes.Saved.createRoute(
                                    imagePath = event.imagePath,
                                    isForSuitChanger = true
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(25.dp))

                BackgroundTextTopBar(
                    isSaving = uiState.isSaving,
                    onBackClick = {
                        viewModel.unselectAll()
                        navController.popBackStack()
                    },
                    onDoneClick = {
                        viewModel.unselectAll()

                        scope.launch {
                            val bitmap = captureLayer
                                .toImageBitmap()
                                .asAndroidBitmap()

                            viewModel.onDoneClick(bitmap)
                        }
                    }
                )

                if (showBannerAd) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        when (bannerState) {
                            is LanguageBannerState.Loaded -> {
                                BannerAd(
                                    adView = (bannerState as LanguageBannerState.Loaded).adView,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            is LanguageBannerState.Loading -> {
                                BannerAdShimmer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                )
                            }

                            else -> {}
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(WhiteColor),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(WhiteColor)
                            .pointerInput(uiState.stickers.size) {
                                detectTapGestures(
                                    onTap = {
                                        viewModel.unselectAll()
                                    }
                                )
                            }
                            .drawWithContent {
                                captureLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(captureLayer)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.backgroundPath.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(uiState.backgroundPath),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Image(
                            painter = rememberAsyncImagePainter(uiState.imagePath),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        uiState.stickers.forEach { sticker ->
                            key(sticker.id) {
                                EditableSticker(
                                    sticker = sticker,
                                    onSelect = {
                                        viewModel.selectSticker(sticker.id)
                                    },
                                    onDeleteClick = {
                                        viewModel.deleteSticker(sticker.id)
                                    },
                                    onTransform = { pan, zoom, rotation ->
                                        viewModel.updateStickerTransform(
                                            id = sticker.id,
                                            pan = pan,
                                            zoom = zoom,
                                            rotation = rotation
                                        )
                                    },
                                    onResizeDrag = { delta ->
                                        viewModel.updateStickerScaleByDelta(
                                            id = sticker.id,
                                            delta = delta
                                        )
                                    },
                                    onRotateDrag = { delta ->
                                        viewModel.updateStickerRotationByDelta(
                                            id = sticker.id,
                                            delta = delta
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                BackgroundTextBottomBar(
                    onBackgroundClick = {
                        viewModel.unselectAll()
                        showBackgroundSheet = true
                    },
                    onTextClick = {
                        viewModel.showAddTextDialog()
                    },
                    onStickerClick = {
                        viewModel.showStickerSheet()
                    }
                )
            }
        }

        uiState.selectedTextSticker?.let { selectedTextSticker ->
            TextEditingBottomSheet(
                selectedTextSticker = selectedTextSticker,
                selectedTab = uiState.selectedTextEditingTab,
                onTabClick = viewModel::updateTextEditingTab,
                onFontClick = viewModel::updateSelectedFont,
                onColorClick = viewModel::updateSelectedTextColor,
                onShadowClick = viewModel::updateSelectedShadow,
                onAlignClick = viewModel::updateSelectedAlignment,
                onCollapseClick = {
                    viewModel.unselectAll()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

    }
    if (showBackgroundSheet) {
        BackgroundBottomSheet(
            selectedBackgroundPath = uiState.backgroundPath,
            onDismiss = {
                showBackgroundSheet = false
            },
            onBackgroundSelected = { backgroundUrl ->
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<String>(SELECTED_BACKGROUND_IMAGE_KEY)

                viewModel.updateBackground(backgroundUrl)
                showBackgroundSheet = false
            },
            onGalleryClick = {
                if (GalleryPermissionHelper.hasGalleryPermission(context)) {
                    navController.navigate(AppRoutes.GalleryForBackground.route)
                } else {
                    navController.navigate(AppRoutes.GalleryPermissionForBackground.createRoute())
                }
            }
        )
    }
    if (uiState.showStickerSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.hideStickerSheet()
            },
            containerColor = WhiteColor,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
        ) {
            StickerBottomSheet(
                onStickerClick = viewModel::addEmojiSticker,
                onCloseClick = {
                    viewModel.hideStickerSheet()
                }
            )
        }
    }

    if (uiState.showAddTextDialog) {
        AddTextDialog(
            value = uiState.inputText,
            onValueChange = viewModel::onInputTextChange,
            onCancelClick = {
                viewModel.hideAddTextDialog()
            },
            onAddClick = {
                viewModel.addTextSticker()
            }
        )
    }
}

@Composable
private fun BackgroundTextTopBar(
    isSaving: Boolean,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(HomeBackgroundColor)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickableNoRipple {
                    onBackClick()
                },
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
            text = stringResource(R.string.background_and_text),
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onDoneClick,
            enabled = !isSaving,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(64.dp)
                .height(32.dp)
        ) {
            Text(
                text = if (isSaving) "..." else stringResource(R.string.save),
                color = WhiteColor,
                fontFamily = SfProDisplayBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun BackgroundTextBottomBar(
    onBackgroundClick: () -> Unit,
    onTextClick: () -> Unit,
    onStickerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(WhiteColor)
            .padding(horizontal = 32.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackgroundTextBottomItem(
            icon = R.drawable.ic_background,
            title = stringResource(R.string.background),
            onClick = onBackgroundClick
        )

        BackgroundTextBottomItem(
            icon = R.drawable.ic_add_text,
            title = stringResource(R.string.add_text),
            onClick = onTextClick
        )

        BackgroundTextBottomItem(
            icon = R.drawable.ic_stickers,
            title = stringResource(R.string.stickers),
            onClick = onStickerClick
        )
    }
}

@Composable
private fun BackgroundTextBottomItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickableNoRipple {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = TextColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontFamily = SfProDisplayRegular,
            fontSize = 10.sp,
            color = SubTextColor
        )
    }
}
