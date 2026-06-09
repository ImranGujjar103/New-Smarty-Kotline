package com.imr.example.newsmartykotlin.presentation.backgroundtext.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor

@Composable
fun CustomColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var value by remember { mutableFloatStateOf(1f) }

    val selectedColor = remember(hue, saturation, value) {
        Color.hsv(
            hue = hue,
            saturation = saturation,
            value = value
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Color",
                fontFamily = SfProDisplayBold,
                color = TextColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    SaturationValuePicker(
                        hue = hue,
                        saturation = saturation,
                        value = value,
                        onChanged = { newSaturation, newValue ->
                            saturation = newSaturation
                            value = newValue
                            onColorSelected(
                                Color.hsv(hue, newSaturation, newValue)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                HueSlider(
                    hue = hue,
                    onHueChanged = { newHue ->
                        hue = newHue
                        onColorSelected(
                            Color.hsv(newHue, saturation, value)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = selectedColor.toHex(),
                        color = TextColor,
                        fontSize = 14.sp,
                        fontFamily = SfProDisplayBold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                }
            ) {
                Text(
                    text = "Done",
                    color = PrimaryColor,
                    fontFamily = SfProDisplayBold
                )
            }
        }
    )
}

@Composable
private fun SaturationValuePicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onChanged: (Float, Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val newSaturation =
                            (offset.x / size.width).coerceIn(0f, 1f)

                        val newValue =
                            (1f - offset.y / size.height).coerceIn(0f, 1f)

                        onChanged(newSaturation, newValue)
                    },
                    onDrag = { change, _ ->
                        val newSaturation =
                            (change.position.x / size.width).coerceIn(0f, 1f)

                        val newValue =
                            (1f - change.position.y / size.height).coerceIn(0f, 1f)

                        onChanged(newSaturation, newValue)
                        change.consume()
                    }
                )
            }
    ) {
        val baseColor = Color.hsv(hue, 1f, 1f)

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.White, baseColor)
            )
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black)
            )
        )

        val indicatorX = saturation * size.width
        val indicatorY = (1f - value) * size.height

        drawCircle(
            color = Color.White,
            radius = 10.dp.toPx(),


            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 3.dp.toPx())
        )

        drawCircle(
            color = Color.Black,
            radius = 12.dp.toPx(),
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueChanged: (Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .clip(RoundedCornerShape(50))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onHueChanged(
                            ((offset.x / size.width) * 360f).coerceIn(0f, 360f)
                        )
                    },
                    onDrag = { change, _ ->
                        onHueChanged(
                            ((change.position.x / size.width) * 360f).coerceIn(0f, 360f)
                        )
                        change.consume()
                    }
                )
            }
    ) {
        val hueColors = listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        )

        drawRoundRect(
            brush = Brush.horizontalGradient(hueColors),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(50f, 50f)
        )

        val indicatorX = (hue / 360f) * size.width

        drawCircle(
            color = Color.White,
            radius = 10.dp.toPx(),
            center = Offset(indicatorX, size.height / 2f),
            style = Stroke(width = 3.dp.toPx())
        )

        drawCircle(
            color = Color.Black,
            radius = 12.dp.toPx(),
            center = Offset(indicatorX, size.height / 2f),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun Color.toHex(): String {
    val red = (red * 255).roundToInt()
    val green = (green * 255).roundToInt()
    val blue = (blue * 255).roundToInt()

    return "#%02X%02X%02X".format(red, green, blue)
}