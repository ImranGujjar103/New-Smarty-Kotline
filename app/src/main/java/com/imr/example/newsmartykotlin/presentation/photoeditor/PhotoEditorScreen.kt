package com.imr.example.newsmartykotlin.presentation.photoeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import android.app.Activity
import com.imr.example.newsmartykotlin.presentation.common.components.BannerAd
import com.imr.example.newsmartykotlin.presentation.common.components.BannerAdShimmer
import com.imr.example.newsmartykotlin.presentation.language.LanguageBannerState
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.utils.BitmapUtils
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.navigation.ERASED_IMAGE_RESULT_KEY
import com.imr.example.newsmartykotlin.presentation.navigation.SELECTED_SUIT_URL_KEY
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

private enum class EditableLayer {
    SUIT,
    FACE
}

private val OffsetSaver = Saver<MutableState<Offset>, List<Float>>(
    save = { listOf(it.value.x, it.value.y) },
    restore = { mutableStateOf(Offset(it[0], it[1])) }
)

private val FloatStateSaver = Saver<MutableFloatState, Float>(
    save = { it.floatValue },
    restore = { mutableFloatStateOf(it) }
)

@Composable
fun PhotoEditorScreen(
    navController: NavController,
    onActionClick: (EditorAction) -> Unit,
    viewModel: PhotoEditorViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val captureLayer = rememberGraphicsLayer()

    val uiState by viewModel.uiState.collectAsState()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showBannerAd = config.photoEditorBanner.toShow && !isPurchased && isConnected
    val bannerState by adViewModel.getBannerAdState("PhotoEditorBanner").collectAsStateWithLifecycle()

    LaunchedEffect(showBannerAd) {
        if (showBannerAd) {
            adViewModel.loadBannerAd(
                activity = context as Activity,
                adId = config.photoEditorBanner.adId,
                tag = "PhotoEditorBanner"
            )
        }
    }

    val selectedSuitUrlFlow = remember {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<String?>(SELECTED_SUIT_URL_KEY, null)
    }

    val selectedSuitUrl by selectedSuitUrlFlow?.collectAsState() ?: remember {
        mutableStateOf(null)
    }
    val erasedImageFlow = remember {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<String?>(ERASED_IMAGE_RESULT_KEY, null)
    }

    val erasedImageUri by erasedImageFlow?.collectAsState() ?: remember {
        mutableStateOf(null)
    }

    LaunchedEffect(erasedImageUri) {
        erasedImageUri?.let { uri ->
            viewModel.onEraserDone(uri)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(ERASED_IMAGE_RESULT_KEY, null)
        }
    }
    LaunchedEffect(selectedSuitUrl) {
        selectedSuitUrl?.let { newSuitUrl ->
            viewModel.onSuitChanged(newSuitUrl)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set(SELECTED_SUIT_URL_KEY, null)
        }
    }

    val suitFlipX = rememberSaveable(saver = FloatStateSaver) { mutableFloatStateOf(1f) }
    val faceFlipX = rememberSaveable(saver = FloatStateSaver) { mutableFloatStateOf(1f) }

    var selectedLayer by rememberSaveable { mutableStateOf(EditableLayer.FACE) }

    val suitOffset = rememberSaveable(saver = OffsetSaver) { mutableStateOf(Offset.Zero) }
    val suitScale = rememberSaveable(saver = FloatStateSaver) { mutableFloatStateOf(1f) }
    val suitRotation = rememberSaveable(saver = FloatStateSaver) { mutableFloatStateOf(0f) }

    val faceOffset = rememberSaveable(saver = OffsetSaver) { mutableStateOf(Offset(0f, -90f)) }
    val faceScale = rememberSaveable(saver = FloatStateSaver) { mutableFloatStateOf(1f) }
    val faceRotation = rememberSaveable(saver = FloatStateSaver) { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    )
    {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            PhotoEditorTopBar(
                onBackClick = { navController.popBackStack() },
                onNextClick = {
                    scope.launch {
                        val bitmap = captureLayer
                            .toImageBitmap()
                            .asAndroidBitmap()

                        val imagePath = BitmapUtils.saveBitmapToCache(
                            context = context,
                            bitmap = bitmap
                        )
                        navController.navigate(
                            AppRoutes.BackgroundText.createRoute(imagePath)
                        )
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
                    .background(WhiteColor)
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .clipToBounds()
                        .drawWithContent {
                            captureLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(captureLayer)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val parentWidthDp = maxWidth
                    val parentHeightDp = maxHeight

                    val faceWidthDp = minOf(122.dp, parentWidthDp * 0.35f)
                    val faceHeightDp = minOf(150.dp, parentHeightDp * 0.28f)

                    TransformableEditorImage(
                        imageUri = uiState.faceImageUri.toUri(),
                        selected = selectedLayer == EditableLayer.FACE,
                        offsetState = faceOffset,
                        scaleState = faceScale,
                        rotationState = faceRotation,
                        flipXState = faceFlipX,
                        minScale = 0.4f,
                        maxScale = 4f,
                        modifier = Modifier
                            .width(faceWidthDp)
                            .height(faceHeightDp)
                    )

                    if (uiState.suitUrl.isNotEmpty()) {
                        TransformableEditorImage(
                            imageUri = uiState.suitUrl,
                            selected = selectedLayer == EditableLayer.SUIT,
                            offsetState = suitOffset,
                            scaleState = suitScale,
                            rotationState = suitRotation,
                            flipXState = suitFlipX,
                            minScale = 0.6f,
                            maxScale = 3f,
                            modifier = Modifier
                                .requiredSizeIn(
                                    maxWidth = parentWidthDp,
                                    maxHeight = parentHeightDp
                                )
                                .wrapContentSize()
                        )
                    }
                }

                FloatingLayerButton(
                    selectedLayer = selectedLayer,
                    onClick = {
                        selectedLayer = if (selectedLayer == EditableLayer.FACE) {
                            EditableLayer.SUIT
                        } else {
                            EditableLayer.FACE
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 28.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            EditorBottomBar { action ->
                when (action) {
                    EditorAction.ERASER -> {
                        navController.navigate(
                            AppRoutes.Eraser.createRoute(
                                faceImageUri = uiState.faceImageUri
                            )
                        )
                    }

                    EditorAction.FACE_FLIP -> {
                        faceFlipX.floatValue *= -1f
                    }

                    EditorAction.SUIT_FLIP -> {
                        suitFlipX.floatValue *= -1f
                    }

                    else -> {
                        onActionClick(action)
                    }
                }
            }
        }
    }
}

@Composable
private fun TransformableEditorImage(
    imageUri: Any,
    selected: Boolean,
    offsetState: MutableState<Offset>,
    scaleState: MutableFloatState,
    rotationState: MutableFloatState,
    flipXState: MutableFloatState,
    modifier: Modifier = Modifier,
    minScale: Float = 0.6f,
    maxScale: Float = 3f
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetState.value.x
                translationY = offsetState.value.y

                // keep parent scale always positive
                scaleX = scaleState.floatValue
                scaleY = scaleState.floatValue

                rotationZ = rotationState.floatValue
            }
            .then(
                if (selected) {
                    Modifier.pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotate ->
                            offsetState.value += pan

                            scaleState.floatValue =
                                (scaleState.floatValue * zoom).coerceIn(minScale, maxScale)

                            rotationState.floatValue += rotate
                        }
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // flip only bitmap/image content
                    scaleX = flipXState.floatValue
                    scaleY = 1f
                },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun FloatingLayerButton(
    selectedLayer: EditableLayer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(PrimaryColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                id = if (selectedLayer == EditableLayer.FACE) {
                    R.drawable.ic_face
                } else {
                    R.drawable.ic_suit
                }
            ),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PhotoEditorTopBar(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
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
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
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
            text = stringResource(R.string.photo_editor),
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onNextClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(60.dp)
                .height(30.dp)
        ) {
            Text(
                text = stringResource(R.string.next),
                color = WhiteColor,
                fontSize = 14.sp,
                fontFamily = SfProDisplayBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EditorBottomBar(
    onActionClick: (EditorAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(WhiteColor)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        EditorBottomItem(
            icon = R.drawable.ic_out_fits,
            title = stringResource(R.string.outfits)
        ) {
            onActionClick(EditorAction.OUTFITS)
        }

        EditorBottomItem(
            icon = R.drawable.ic_earser,
            title = stringResource(R.string.eraser)
        ) {
            onActionClick(EditorAction.ERASER)
        }

        EditorBottomItem(
            icon = R.drawable.ic_face_flip,
            title = stringResource(R.string.face_flip)
        ) {
            onActionClick(EditorAction.FACE_FLIP)
        }

        EditorBottomItem(
            icon = R.drawable.ic_suit_flip,
            title = stringResource(R.string.suit_flip)
        ) {
            onActionClick(EditorAction.SUIT_FLIP)
        }
    }
}
@Composable
private fun EditorBottomItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
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
enum class EditorAction {
    OUTFITS,
    ERASER,
    FACE_FLIP,
    SUIT_FLIP
}
