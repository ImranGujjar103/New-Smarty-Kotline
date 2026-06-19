package com.imr.example.newsmartykotlin.presentation.passport.cropper

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun PassportCropperScreen(
    navController: NavController,
    viewModel: PassportCropperViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                PassportCropperEvent.NavigateBack -> navController.popBackStack()

                is PassportCropperEvent.NavigateToBackgroundRemove -> {
                    navController.navigate(
                        AppRoutes.PassportBgRemove.createRoute(
                            croppedImageUri = event.croppedImageUri,
                            countryId = event.countryId,
                            documentType = event.documentType
                        )
                    )
                }
            }
        }
    }

    var imageScale by remember { mutableStateOf(1f) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var baseImageRect by remember { mutableStateOf(RectF()) }
    var cropRect by remember { mutableStateOf(RectF()) }

    val transformedImageRect = remember(baseImageRect, imageScale, imageOffset) {
        val width = baseImageRect.width() * imageScale
        val height = baseImageRect.height() * imageScale
        val left = baseImageRect.left + imageOffset.x - (width - baseImageRect.width()) / 2f
        val top = baseImageRect.top + imageOffset.y - (height - baseImageRect.height()) / 2f
        RectF(left, top, left + width, top + height)
    }

    LaunchedEffect(transformedImageRect, cropRect) {
        if (transformedImageRect.width() > 0 && cropRect.width() > 0) {
            viewModel.onCropAreaChanged(
                imageBounds = transformedImageRect,
                cropRect = cropRect
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
        Spacer(modifier = Modifier.height(25.dp))

        PassportCropperTopBar(
            onBackClick = viewModel::onBackClick,
            onContinueClick = viewModel::onContinueClick
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        imageScale *= zoom
                        imageOffset += pan
                    }
                }
        ) {


            Image(
                painter = rememberAsyncImagePainter(uiState.imageUri.toUri()),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInRoot()

                        baseImageRect = RectF(
                            position.x,
                            position.y,
                            position.x + coordinates.size.width,
                            position.y + coordinates.size.height
                        )
                    }
                    .graphicsLayer(
                        scaleX = imageScale,
                        scaleY = imageScale,
                        translationX = imageOffset.x,
                        translationY = imageOffset.y
                    )
            )

            PassportFixedCropFrame(
                modifier = Modifier
                    .align(Alignment.Center),
                inchText = uiState.inchText,
                passportPixel = uiState.pixelText,
                onCropRectReady = { rect ->
                    cropRect = rect
                }
            )

            if (uiState.isCropping) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }
        }
    }
}

@Composable
private fun PassportCropperTopBar(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .background(HomeBackgroundColor)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.crop),
            fontFamily = SfProDisplayBold,
            color = TextColor,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onContinueClick,
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
private fun PassportFixedCropFrame(
    modifier: Modifier = Modifier,
    inchText: String,
    passportPixel: String,
    onCropRectReady: (RectF) -> Unit
) {
    val pixelSize = remember(passportPixel) {
        parsePassportPixel(passportPixel)
    }

    val aspectRatio = remember(pixelSize) {
        pixelSize.first.toFloat() / pixelSize.second.toFloat()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxFrameWidth = maxWidth.coerceAtMost(320.dp)
        val frameHeight = maxFrameWidth / aspectRatio

        Box(
            modifier = Modifier
                .width(maxFrameWidth)
                .height(frameHeight)
                .onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInRoot()

                    onCropRectReady(
                        RectF(
                            position.x,
                            position.y,
                            position.x + coordinates.size.width,
                            position.y + coordinates.size.height
                        )
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                drawRect(
                    color = Color.White,
                    style = Stroke(width = 2f)
                )

                val r = 6.dp.toPx()

                drawCircle(Color.White, r, Offset(0f, 0f))
                drawCircle(Color.White, r, Offset(size.width, 0f))
                drawCircle(Color.White, r, Offset(0f, size.height))
                drawCircle(Color.White, r, Offset(size.width, size.height))
            }
        }
    }
}

private fun parsePassportPixel(pixelText: String): Pair<Int, Int> {
    val regex = Regex("""(\d+)\s*x\s*(\d+)""")
    val match = regex.find(pixelText)

    val width = match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 413
    val height = match?.groupValues?.getOrNull(2)?.toIntOrNull() ?: 531

    return width to height
}