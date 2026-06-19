package com.imr.example.newsmartykotlin.presentation.passport.background

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.backgroundtext.components.CustomColorPickerDialog
import com.imr.example.newsmartykotlin.presentation.navigation.RESULT_UPDATED_IMAGE_URI
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun PassportBackgroundScreen(
    navController: NavController,
    viewModel: PassportBackgroundViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val view = LocalView.current
    var captureBounds by remember { mutableStateOf<RectF?>(null) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                PassportBackgroundEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                PassportBackgroundEvent.CaptureAndDone -> {
                    val uri = captureBounds?.let {
                        captureViewAreaToCache(
                            context = context,
                            view = view,
                            bounds = it
                        )
                    }

                    uri?.let {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(RESULT_UPDATED_IMAGE_URI, it.toString())
                    }

                    navController.popBackStack()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PassportBackgroundTopBar(
                onBackClick = viewModel::onBackClick,
                onDoneClick = viewModel::onDoneClick
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(350.dp)
                        .background(uiState.selectedColor)
                        .onGloballyPositioned { coordinates ->
                            val rect = coordinates.boundsInWindow()

                            captureBounds = RectF(
                                rect.left,
                                rect.top,
                                rect.right,
                                rect.bottom
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.imageUri.toUri()),
                        contentDescription = stringResource(R.string.passport_background_preview),
                        modifier = Modifier.fillMaxSize(),

                    )
                }
            }
        }

        PassportBackgroundBottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedColor = uiState.selectedColor,
            isExpanded = uiState.isBottomSheetExpanded,
            onBackClick = viewModel::onBackClick,
            onToggleClick = viewModel::onBottomSheetToggle,
            onColorSelected = viewModel::onColorSelected
        )
    }
}
private fun captureViewAreaToCache(
    context: Context,
    view: View,
    bounds: RectF
): Uri {
    val fullBitmap = view.drawToBitmap(Bitmap.Config.ARGB_8888)

    val left = bounds.left.roundToInt().coerceIn(0, fullBitmap.width)
    val top = bounds.top.roundToInt().coerceIn(0, fullBitmap.height)
    val right = bounds.right.roundToInt().coerceIn(left + 1, fullBitmap.width)
    val bottom = bounds.bottom.roundToInt().coerceIn(top + 1, fullBitmap.height)

    val croppedBitmap = Bitmap.createBitmap(
        fullBitmap,
        left,
        top,
        right - left,
        bottom - top
    )

    val file = File(
        context.cacheDir,
        "passport_background_${System.currentTimeMillis()}.png"
    )

    FileOutputStream(file).use { output ->
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }

    return file.toUri()
}
@Composable
private fun PassportBackgroundTopBar(
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomeBackgroundColor)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 20.dp)
            .padding(top = 25.dp, bottom = 80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.back),
                tint = WhiteColor,
                modifier = Modifier.size(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = stringResource(R.string.background),
            color = TextColor,
            fontSize = 18.sp,
            fontFamily = SfProDisplayBold,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable { onDoneClick() }
                .width(60.dp).height(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.done),
                color = WhiteColor,
                fontSize = 13.sp,
                fontFamily = SfProDisplayBold
            )
        }
    }
}

@Composable
private fun PassportBackgroundBottomSheet(
    modifier: Modifier = Modifier,
    selectedColor: Color,
    isExpanded: Boolean,
    onBackClick: () -> Unit,
    onToggleClick: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(WhiteColor)
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            .padding(vertical = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = stringResource(R.string.back),
                tint = TextColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = stringResource(R.string.background),
                color = TextColor,
                fontSize = 13.sp,
                fontFamily = SfProDisplayBold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Icon(
                painter = painterResource(
                    if (isExpanded) R.drawable.ic_close_bottom_sheet else R.drawable.ic_arrow_up
                ),
                contentDescription = stringResource(R.string.expand_collapse),
                tint = TextColor,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onToggleClick() }
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.size(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientColorCircle(
                    isSelected = true,
                    onClick = {
                        showColorPicker = true
                    }
                )

                backgroundColors().forEach { color ->
                    SolidColorCircle(
                        color = color,
                        isSelected = color.toArgb() == selectedColor.toArgb(),
                        onClick = { onColorSelected(color) }
                    )
                }
            }
        }

        if (showColorPicker) {
            CustomColorPickerDialog(
                initialColor = selectedColor,
                onDismiss = {
                    showColorPicker = false
                },
                onColorSelected = { color ->
                    onColorSelected(color)
                }
            )
        }
    }
}

@Composable
private fun SolidColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(31.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = PrimaryColor,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}

@Composable
private fun GradientColorCircle(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(31.dp)
            .clip(CircleShape)
            .background(
                Brush.sweepGradient(
                    listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )
                )
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = PrimaryColor,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}

private fun backgroundColors(): List<Color> {
    return listOf(
        Color.White,
        Color(0xFFFF5252),
        Color(0xFFFF8A24),
        Color(0xFFFFDF18),
        Color(0xFF43E21B),
        Color(0xFF2BCFA7),
        Color(0xFF1CB8C9),
        Color(0xFF2B72E8),
        Color(0xFF9C3DF4),
        Color(0xFFE742A9)
    )
}