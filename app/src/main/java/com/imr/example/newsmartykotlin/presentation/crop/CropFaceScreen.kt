package com.imr.example.newsmartykotlin.presentation.crop

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.CropAspectRatio
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.AppTypography
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.RedColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.SubTextColor
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel


@Composable
fun CropFaceScreen(
    navController: NavController,
    viewModel: CropFaceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                CropFaceEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is CropFaceEvent.NavigateNext -> {
                    navController.navigate(
                        AppRoutes.BgRemove.createRoute(
                            suitUrl = event.suitId,
                            croppedImageUri = event.croppedImageUri
                        )
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CropTopBar(
            isLoading = uiState.isCropping,
            onBackClick = viewModel::onBackClick,
            onNextClick = viewModel::onNextClick
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = RedColor,
                style = AppTypography.Body,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))


        CropImageArea(
            imageUri = uiState.imageUri,
            selectedRatio = uiState.selectedRatio,
            onCropChanged = viewModel::onCropAreaChanged,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        CropRatioBar(
            selectedRatio = uiState.selectedRatio,
            onRatioClick = viewModel::onRatioSelected
        )
    }
}

@Composable
private fun CropTopBar(
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(CardColor)
            .padding(horizontal = 22.dp),
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
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = stringResource(R.string.crop_face),
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onNextClick,
            enabled = !isLoading,
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
            if (isLoading) {
                CircularProgressIndicator(
                    color = WhiteColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
            } else {
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
}

@Composable
private fun CropImageArea(
    imageUri: String,
    selectedRatio: CropAspectRatio,
    onCropChanged: (RectF, RectF) -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var cropRect by remember { mutableStateOf<Rect?>(null) }

    val handleRadiusPx = with(LocalDensity.current) { 5.dp.toPx() }
    val minCropSizePx = with(LocalDensity.current) { 120.dp.toPx() }

    val imageBounds = remember(containerSize) {
        if (containerSize.width == 0 || containerSize.height == 0) {
            Rect.Zero
        } else {
            Rect(
                left = 0f,
                top = 0f,
                right = containerSize.width.toFloat(),
                bottom = containerSize.height.toFloat()
            )
        }
    }

    LaunchedEffect(containerSize, selectedRatio) {
        if (containerSize.width > 0 && containerSize.height > 0) {
            val width = containerSize.width.toFloat()
            val height = containerSize.height.toFloat()

            cropRect = when (selectedRatio) {

                CropAspectRatio.ORIGINAL -> {
                    Rect(
                        left = 0f,
                        top = 0f,
                        right = width,
                        bottom = height
                    )
                }

                CropAspectRatio.FREE -> {
                    val size = minOf(width, height) * 0.65f
                    val left = (width - size) / 2f
                    val top = (height - size) / 2f

                    Rect(
                        offset = Offset(left, top),
                        size = Size(size, size)
                    )
                }

                else -> {
                    val ratio = selectedRatio.ratio ?: 1f
                    val maxW = width * 0.72f
                    val maxH = height * 0.55f

                    val cropWidth: Float
                    val cropHeight: Float

                    if (maxW / maxH > ratio) {
                        cropHeight = maxH
                        cropWidth = cropHeight * ratio
                    } else {
                        cropWidth = maxW
                        cropHeight = cropWidth / ratio
                    }

                    val left = (width - cropWidth) / 2f
                    val top = (height - cropHeight) / 2f

                    Rect(
                        offset = Offset(left, top),
                        size = Size(cropWidth, cropHeight)
                    )
                }
            }
        }
    }

    LaunchedEffect(cropRect, imageBounds) {
        val rect = cropRect
        if (rect != null && imageBounds != Rect.Zero) {
            onCropChanged(
                RectF(
                    imageBounds.left,
                    imageBounds.top,
                    imageBounds.right,
                    imageBounds.bottom
                ),
                RectF(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom
                )
            )
        }
    }

    Box(
        modifier = modifier
            .background(WhiteColor)
            .onSizeChanged {
                containerSize = it
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri.toUri()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )

        val currentCropRect = cropRect

        if (currentCropRect != null) {
            CropOverlay(
                cropRect = currentCropRect,
                imageBounds = imageBounds,
                selectedRatio = selectedRatio,
                handleRadiusPx = handleRadiusPx,
                minCropSizePx = minCropSizePx,
                onCropRectChange = {
                    cropRect = it
                }
            )
        }
    }
}

@Composable
private fun CropOverlay(
    cropRect: Rect,
    imageBounds: Rect,
    selectedRatio: CropAspectRatio,
    handleRadiusPx: Float,
    minCropSizePx: Float,
    onCropRectChange: (Rect) -> Unit
) {
    var currentRect by remember { mutableStateOf(cropRect) }
    var dragMode by remember { mutableStateOf(CropDragMode.NONE) }

    val isOriginal = selectedRatio == CropAspectRatio.ORIGINAL
    val isFree = selectedRatio == CropAspectRatio.FREE
    val canMove = !isOriginal
    val canResize = isFree

    LaunchedEffect(cropRect) {
        currentRect = cropRect
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(selectedRatio, imageBounds) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragMode = detectDragMode(
                            touch = offset,
                            rect = currentRect,
                            handleTouchRadius = handleRadiusPx * 6f,
                            canMove = canMove,
                            canResize = canResize
                        )
                    },
                    onDragEnd = {
                        dragMode = CropDragMode.NONE
                        onCropRectChange(currentRect)
                    },
                    onDragCancel = {
                        dragMode = CropDragMode.NONE
                        onCropRectChange(currentRect)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        val updatedRect = updateCropRect(
                            rect = currentRect,
                            dragMode = dragMode,
                            dragAmount = dragAmount,
                            bounds = imageBounds,
                            minSize = minCropSizePx,
                            canResize = canResize
                        )

                        currentRect = updatedRect
                        onCropRectChange(updatedRect)
                    }
                )
            }
    ) {
        val overlayPath = Path().apply {
            fillType = PathFillType.EvenOdd

            addRect(
                Rect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height
                )
            )

            addRect(currentRect)
        }

        drawPath(
            path = overlayPath,
            color = Color.Black.copy(alpha = 0.55f)
        )

        drawRect(
            color = Color.White,
            topLeft = Offset(currentRect.left, currentRect.top),
            size = Size(currentRect.width, currentRect.height),
            style = Stroke(width = 2f)
        )

        if (canResize) {
            listOf(
                Offset(currentRect.left, currentRect.top),
                Offset(currentRect.right, currentRect.top),
                Offset(currentRect.left, currentRect.bottom),
                Offset(currentRect.right, currentRect.bottom)
            ).forEach { point ->
                drawCircle(
                    color = Color.White,
                    radius = handleRadiusPx,
                    center = point
                )
            }
        }
    }
}

private enum class CropDragMode {
    NONE,
    MOVE,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

private fun detectDragMode(
    touch: Offset,
    rect: Rect,
    handleTouchRadius: Float,
    canMove: Boolean,
    canResize: Boolean
): CropDragMode {
    if (canResize) {
        val topLeft = Offset(rect.left, rect.top)
        val topRight = Offset(rect.right, rect.top)
        val bottomLeft = Offset(rect.left, rect.bottom)
        val bottomRight = Offset(rect.right, rect.bottom)

        when {
            touch.distanceTo(topLeft) <= handleTouchRadius -> return CropDragMode.TOP_LEFT
            touch.distanceTo(topRight) <= handleTouchRadius -> return CropDragMode.TOP_RIGHT
            touch.distanceTo(bottomLeft) <= handleTouchRadius -> return CropDragMode.BOTTOM_LEFT
            touch.distanceTo(bottomRight) <= handleTouchRadius -> return CropDragMode.BOTTOM_RIGHT
        }
    }

    return if (canMove && rect.contains(touch)) {
        CropDragMode.MOVE
    } else {
        CropDragMode.NONE
    }
}

private fun updateCropRect(
    rect: Rect,
    dragMode: CropDragMode,
    dragAmount: Offset,
    bounds: Rect,
    minSize: Float,
    canResize: Boolean
): Rect {

    if (dragMode == CropDragMode.NONE) return rect

    if (dragMode == CropDragMode.MOVE) {

        val maxLeft = bounds.right - rect.width
        val maxTop = bounds.bottom - rect.height

        val newLeft = (rect.left + dragAmount.x)
            .coerceIn(bounds.left, maxLeft)

        val newTop = (rect.top + dragAmount.y)
            .coerceIn(bounds.top, maxTop)

        return Rect(
            offset = Offset(newLeft, newTop),
            size = Size(rect.width, rect.height)
        )
    }

    if (!canResize) return rect

    var left = rect.left
    var top = rect.top
    var right = rect.right
    var bottom = rect.bottom

    when (dragMode) {

        CropDragMode.TOP_LEFT -> {
            left += dragAmount.x
            top += dragAmount.y
        }

        CropDragMode.TOP_RIGHT -> {
            right += dragAmount.x
            top += dragAmount.y
        }

        CropDragMode.BOTTOM_LEFT -> {
            left += dragAmount.x
            bottom += dragAmount.y
        }

        CropDragMode.BOTTOM_RIGHT -> {
            right += dragAmount.x
            bottom += dragAmount.y
        }

        else -> Unit
    }

    // Prevent inversion
    if (right - left < minSize) {
        when (dragMode) {
            CropDragMode.TOP_LEFT,
            CropDragMode.BOTTOM_LEFT -> {
                left = right - minSize
            }

            CropDragMode.TOP_RIGHT,
            CropDragMode.BOTTOM_RIGHT -> {
                right = left + minSize
            }

            else -> Unit
        }
    }

    if (bottom - top < minSize) {
        when (dragMode) {
            CropDragMode.TOP_LEFT,
            CropDragMode.TOP_RIGHT -> {
                top = bottom - minSize
            }

            CropDragMode.BOTTOM_LEFT,
            CropDragMode.BOTTOM_RIGHT -> {
                bottom = top + minSize
            }

            else -> Unit
        }
    }

    // Clamp inside bounds
    left = left.coerceAtLeast(bounds.left)
    top = top.coerceAtLeast(bounds.top)

    right = right.coerceAtMost(bounds.right)
    bottom = bottom.coerceAtMost(bounds.bottom)

    // Re-check size after clamping
    if (right - left < minSize) {
        right = (left + minSize).coerceAtMost(bounds.right)
        left = right - minSize
    }

    if (bottom - top < minSize) {
        bottom = (top + minSize).coerceAtMost(bounds.bottom)
        top = bottom - minSize
    }

    return Rect(
        left = left,
        top = top,
        right = right,
        bottom = bottom
    )
}

private fun Offset.distanceTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return kotlin.math.sqrt(dx * dx + dy * dy)
}

@Composable
private fun CropRatioBar(
    selectedRatio: CropAspectRatio,
    onRatioClick: (CropAspectRatio) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = WhiteColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CropAspectRatio.entries.forEach { ratio ->
                CropRatioItem(
                    ratio = ratio,
                    selected = selectedRatio == ratio,
                    onClick = {
                        onRatioClick(ratio)
                    }
                )
            }
        }
    }
}

@Composable
private fun CropRatioItem(
    ratio: CropAspectRatio,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(36.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = ratio.iconResId ?: 0),
                contentDescription = ratio.labelResName,
                tint = if (selected) PrimaryColor else TextColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = ratio.labelResName,
            fontFamily = SfProDisplayRegular,
            color = if (selected) PrimaryColor else SubTextColor,
            fontSize = 10.sp
        )
    }
}