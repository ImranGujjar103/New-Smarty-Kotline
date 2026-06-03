package com.imr.example.newsmartykotlin.presentation.eraser

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.ErasePoint
import com.imr.example.newsmartykotlin.domain.model.EraseStroke
import com.imr.example.newsmartykotlin.presentation.navigation.ERASED_IMAGE_RESULT_KEY
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun EraserScreen(
    navController: NavController,
    viewModel: EraserViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            .background(CardColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        EraserTopBar(
            isSaving = uiState.isSaving,
            onBackClick = viewModel::onBackClick,
            onDoneClick = viewModel::onDoneClick
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(WhiteColor)
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = uiState.previewBitmap

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                )

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
                            color = WhiteColor,
                            style = Stroke(
                                width = stroke.brushSize
                            )
                        )
                    }

                    val finger = fingerPoint
                    val brush = brushPreviewPoint

                    if (finger != null && brush != null) {
                        drawLine(
                            color = Color.Red.copy(alpha = 0.65f),
                            start = Offset(finger.x, finger.y),
                            end = Offset(brush.x, brush.y),
                            strokeWidth = 2.dp.toPx()
                        )

                        drawCircle(
                            color = Color.Red.copy(alpha = 0.35f),
                            radius = 10.dp.toPx(),
                            center = Offset(finger.x, finger.y)
                        )

                        drawCircle(
                            color = Color.Red,
                            radius = 3.dp.toPx(),
                            center = Offset(finger.x, finger.y)
                        )

                        drawCircle(
                            color = Color.Red.copy(alpha = 0.28f),
                            radius = uiState.brushSize / 2f,
                            center = Offset(brush.x, brush.y)
                        )

                        drawCircle(
                            color = Color.Red,
                            radius = uiState.brushSize / 2f,
                            center = Offset(brush.x, brush.y),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }

        EraserControlPanel(
            brushSize = uiState.brushSize,
            brushOffset = uiState.brushOffset,
            onBrushSizeChange = viewModel::onBrushSizeChange,
            onBrushOffsetChange = viewModel::onBrushOffsetChange,
            onUndoClick = {
                if (localStrokes.isNotEmpty()) {
                    localStrokes.removeAt(localStrokes.lastIndex)
                    viewModel.undo()
                }
            },
            onRedoClick = {
                viewModel.redo()
                localStrokes.clear()
                localStrokes.addAll(viewModel.currentStrokes())
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
            .height(58.dp)
            .background(CardColor)
            .padding(horizontal = 28.dp),
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
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Photo Editor",
            fontFamily = SfProDisplayBold,
            color = TextColor,
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
                text = if (isSaving) "..." else "Save",
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
    onBrushSizeChange: (Float) -> Unit,
    onBrushOffsetChange: (Float) -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp
                )
            )
            .background(WhiteColor)
            .padding(horizontal = 22.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "↻",
                modifier = Modifier.clickableNoRipple {},
                color = TextColor
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "↶",
                modifier = Modifier.clickableNoRipple {
                    onUndoClick()
                },
                color = TextColor
            )

            Spacer(modifier = Modifier.width(22.dp))

            Text(
                text = "↷",
                modifier = Modifier.clickableNoRipple {
                    onRedoClick()
                },
                color = TextColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EraserSliderRow(
            title = "Size",
            value = brushSize,
            valueRange = 15f..130f,
            onValueChange = onBrushSizeChange
        )

        EraserSliderRow(
            title = "Offset",
            value = brushOffset,
            valueRange = 0f..360f,
            onValueChange = onBrushOffsetChange
        )
    }
}

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
            modifier = Modifier.width(58.dp)
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f)
        )
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