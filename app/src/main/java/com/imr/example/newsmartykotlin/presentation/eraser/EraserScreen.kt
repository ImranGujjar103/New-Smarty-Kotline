package com.imr.example.newsmartykotlin.presentation.eraser

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.CompositingStrategy
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.ErasePoint
import com.imr.example.newsmartykotlin.domain.model.EraseStroke
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.ERASED_IMAGE_RESULT_KEY
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.DisabledTextColor
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@Composable
fun EraserScreen(
    navController: NavController,
    viewModel: EraserViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(graphicsLayer) {
        graphicsLayer.compositingStrategy = CompositingStrategy.Offscreen
    }
    val uiState by viewModel.uiState.collectAsState()

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.eraserNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("EraserBottomNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd) {
        if (showAd) {
            adViewModel.loadNativeAd(
                adId = config.eraserNative.adId,
                tag = "EraserBottomNative"
            ) { _ -> }
        }
    }

    val localStrokes = remember { mutableStateListOf<EraseStroke>() }

    var currentPoints by remember {
        mutableStateOf<List<ErasePoint>>(emptyList())
    }

    var canvasSize by remember {
        mutableStateOf(Size.Zero)
    }

    var fingerPoint by remember {
        mutableStateOf<ErasePoint?>(null)
    }

    var brushPreviewPoint by remember {
        mutableStateOf<ErasePoint?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EraserEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is EraserEvent.Done -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(ERASED_IMAGE_RESULT_KEY, event.imageUri)

                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(uiState.brushOffset, canvasSize) {
        if (canvasSize != Size.Zero) {
            val centerFingerPoint = fingerPoint ?: ErasePoint(
                x = canvasSize.width / 2f,
                y = canvasSize.height / 2f
            )

            fingerPoint = centerFingerPoint

            brushPreviewPoint = ErasePoint(
                x = centerFingerPoint.x,
                y = centerFingerPoint.y - uiState.brushOffset
            )
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
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            EraserTopBar(
                isSaving = uiState.isSaving,
                onBackClick = viewModel::onBackClick,
                onDoneClick = {
                    scope.launch {
                        val bitmap = graphicsLayer
                            .toImageBitmap()
                            .asAndroidBitmap()

                        viewModel.onDoneClick(bitmap)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .checkerboardBackground()
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = uiState.previewBitmap

                if (bitmap != null) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { size ->
                                canvasSize = Size(
                                    width = size.width.toFloat(),
                                    height = size.height.toFloat()
                                )

                                if (fingerPoint == null) {
                                    val initialFinger = ErasePoint(
                                        x = size.width / 2f,
                                        y = size.height / 2f
                                    )

                                    fingerPoint = initialFinger

                                    brushPreviewPoint = ErasePoint(
                                        x = initialFinger.x,
                                        y = initialFinger.y - uiState.brushOffset
                                    )
                                }
                            }
                            .pointerInput(
                                uiState.brushSize,
                                uiState.brushOffset
                            ) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val currentFinger = ErasePoint(
                                            x = offset.x,
                                            y = offset.y
                                        )

                                        val currentBrush = ErasePoint(
                                            x = offset.x,
                                            y = offset.y - uiState.brushOffset
                                        )

                                        fingerPoint = currentFinger
                                        brushPreviewPoint = currentBrush
                                        currentPoints = listOf(currentBrush)
                                    },
                                    onDrag = { change, _ ->
                                        val currentFinger = ErasePoint(
                                            x = change.position.x,
                                            y = change.position.y
                                        )

                                        val currentBrush = ErasePoint(
                                            x = change.position.x,
                                            y = change.position.y - uiState.brushOffset
                                        )

                                        fingerPoint = currentFinger
                                        brushPreviewPoint = currentBrush

                                        currentPoints = currentPoints + currentBrush
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
                                            val stroke = EraseStroke(
                                                points = currentPoints,
                                                brushSize = uiState.brushSize
                                            )

                                            localStrokes.add(stroke)
                                            viewModel.addStroke(stroke)
                                            currentPoints = emptyList()
                                        }
                                    },
                                    onDragCancel = {
                                        currentPoints = emptyList()
                                    }
                                )
                            }
                    ) {
                        val allStrokes = localStrokes + listOfNotNull(
                            currentPoints.takeIf { it.isNotEmpty() }?.let {
                                EraseStroke(
                                    points = it,
                                    brushSize = uiState.brushSize
                                )
                            }
                        )

                        graphicsLayer.record {
                            drawImage(
                                image = bitmap.asImageBitmap(),
                                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                            )

                            allStrokes.forEach { stroke ->
                                val path = Path()

                                stroke.points.forEachIndexed { index, point ->
                                    if (index == 0) {
                                        path.moveTo(point.x, point.y)
                                    } else {
                                        path.lineTo(point.x, point.y)
                                    }
                                }

                                drawPath(
                                    path = path,
                                    color = Color.Transparent,
                                    style = Stroke(
                                        width = stroke.brushSize,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    ),
                                    blendMode = BlendMode.Clear
                                )
                            }
                        }

                        drawLayer(graphicsLayer)

                        brushPreviewPoint?.let { point ->
                            drawCircle(
                                color = PrimaryColor,
                                radius = uiState.brushSize / 2f,
                                center = Offset(point.x, point.y),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }
            }

            EraserControlPanel(
                brushSize = uiState.brushSize,
                brushOffset = uiState.brushOffset,
                canUndo = uiState.canUndo,
                canRedo = uiState.canRedo,
                onBrushSizeChange = viewModel::onBrushSizeChange,
                onBrushOffsetChange = viewModel::onBrushOffsetChange,
                onResetClick = viewModel::showResetDialog,
                onUndoClick = {
                    viewModel.undo()
                    localStrokes.clear()
                    localStrokes.addAll(viewModel.currentStrokes())
                },
                onRedoClick = {
                    viewModel.redo()
                    localStrokes.clear()
                    localStrokes.addAll(viewModel.currentStrokes())
                },
                onBackClick = viewModel::onBackClick
            )
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

    if (uiState.showResetDialog) {
        EraserResetDialog(
            onDismiss = viewModel::hideResetDialog,
            onConfirm = {
                localStrokes.clear()
                currentPoints = emptyList()
                fingerPoint = null
                brushPreviewPoint = null
                viewModel.resetAll()
            }
        )
    }
}

@Composable
private fun EraserTopBar(
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
            text = stringResource(R.string.photo_editor),
            fontFamily = SfProDisplayBold,
            color = TextColor,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onDoneClick,
            enabled = !isSaving,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            ),
            modifier = Modifier
                .width(64.dp)
                .height(32.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = if (isSaving) "..." else stringResource(R.string.save),
                color = WhiteColor,
                fontFamily = SfProDisplayBold
            )
        }
    }
}

@Composable
private fun EraserControlPanel(
    brushSize: Float,
    brushOffset: Float,
    canUndo: Boolean,
    canRedo: Boolean,
    onBrushSizeChange: (Float) -> Unit,
    onBrushOffsetChange: (Float) -> Unit,
    onResetClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.background(WhiteColor)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .graphicsLayer {
                        shadowElevation = 20.dp.toPx()
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp
                        )
                        clip = false

                        ambientShadowColor = Color.Black
                        spotShadowColor = Color.Black
                    }
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp
                        )
                    )
                    .background(WhiteColor)
                    .padding(horizontal = 20.dp, vertical = 19.dp),

                )
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onResetClick() },
                        contentDescription = null,
                        painter = painterResource(R.drawable.ic_reload),
                        tint = TextColor
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = canUndo) {
                                onUndoClick()
                            },
                        contentDescription = null,
                        painter = painterResource(R.drawable.ic_undo),
                        tint = if (canUndo) TextColor else DisabledTextColor
                    )

                    Spacer(modifier = Modifier.width(25.dp))
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = canRedo) {
                                onRedoClick()
                            },
                        contentDescription = null,
                        painter = painterResource(R.drawable.ic_redo),
                        tint = if (canRedo) TextColor else DisabledTextColor
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                    Icon(modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onBackClick() },
                        contentDescription = null,
                        painter = painterResource(R.drawable.ic_close_bottom_sheet),
                        tint = TextColor
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                EraserSliderRow(
                    title = stringResource(R.string.size),
                    value = brushSize,
                    valueRange = 15f..130f,
                    onValueChange = onBrushSizeChange
                )
                Spacer(modifier = Modifier.height(25.dp))
                EraserSliderRow(
                    title = stringResource(R.string.offset),
                    value = brushOffset,
                    valueRange = 0f..360f,
                    onValueChange = onBrushOffsetChange
                )
            }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EraserSliderRow(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = SfProDisplayBold,
            color = TextColor,
            fontSize = 12.sp,
            modifier = Modifier.width(50.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
            ,

            thumb = {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                PrimaryColor,
                                CircleShape
                            )
                    )
                }
            },

            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(4.dp),
                    thumbTrackGapSize = 0.dp,
                    drawStopIndicator = null,
                    colors = SliderDefaults.colors(
                        activeTrackColor = PrimaryColor,
                        inactiveTrackColor = CardColor
                    )
                )
            }
        )
    }
}

@Composable
private fun EraserResetDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    fontFamily = SfProDisplayBold,
                    fontSize = 20.sp,
                    color = TextColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.reset_confirmation),
                    fontSize = 14.sp,
                    color = TextColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardColor)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontFamily = SfProDisplayBold,
                            color = TextColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrimaryColor)
                            .clickable { onConfirm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.reset),
                            fontFamily = SfProDisplayBold,
                            color = WhiteColor
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.checkerboardBackground(
    squareSize: androidx.compose.ui.unit.Dp = 8.dp,
    color1: Color = Color(0xFFE0E0E0),
    color2: Color = Color.White
): Modifier = this.drawBehind {
    val sizePx = squareSize.toPx()
    val columns = (size.width / sizePx).toInt() + 1
    val rows = (size.height / sizePx).toInt() + 1
    for (i in 0 until columns) {
        for (j in 0 until rows) {
            val color = if ((i + j) % 2 == 0) color1 else color2
            drawRect(
                color = color,
                topLeft = Offset(i * sizePx, j * sizePx),
                size = Size(sizePx, sizePx)
            )
        }
    }
}

@Composable
private fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier {
    return this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}
