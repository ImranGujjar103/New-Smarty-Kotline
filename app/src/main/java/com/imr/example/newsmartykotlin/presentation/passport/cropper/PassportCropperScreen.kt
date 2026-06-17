package com.imr.example.newsmartykotlin.presentation.passport.cropper

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

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

    var cropOffset by remember { mutableStateOf(Offset.Zero) }
    var imageRect by remember { mutableStateOf(RectF()) }
    var cropRect by remember { mutableStateOf(RectF()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PassportCropperTopBar(
            onBackClick = viewModel::onBackClick,
            onContinueClick = viewModel::onContinueClick
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {


            Image(
                painter = rememberAsyncImagePainter(uiState.imageUri.toUri()),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInRoot()

                        imageRect = RectF(
                            position.x,
                            position.y,
                            position.x + coordinates.size.width,
                            position.y + coordinates.size.height
                        )

                        viewModel.onCropAreaChanged(
                            imageBounds = imageRect,
                            cropRect = cropRect
                        )
                    }
            )

            PassportFixedCropFrame(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset {
                        IntOffset(
                            x = cropOffset.x.roundToInt(),
                            y = cropOffset.y.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            cropOffset += dragAmount
                        }
                    },
                inchText = uiState.inchText,
                passportPixel = uiState.pixelText,
                onCropRectReady = { rect ->
                    cropRect = rect

                    viewModel.onCropAreaChanged(
                        imageBounds = imageRect,
                        cropRect = cropRect
                    )
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
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .height(70.dp)
            .background(HomeBackgroundColor)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Crop",
            fontFamily = SfProDisplayBold,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onContinueClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = WhiteColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue")
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