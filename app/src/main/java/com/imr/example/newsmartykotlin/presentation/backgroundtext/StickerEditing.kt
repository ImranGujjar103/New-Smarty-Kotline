package com.imr.example.newsmartykotlin.presentation.backgroundtext

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

data class StickerItem(
    val id: Long,
    val value: String,
    val offset: MutableState<Offset> = mutableStateOf(Offset.Zero),
    val scale: MutableFloatState = mutableFloatStateOf(1f),
    val rotation: MutableFloatState = mutableFloatStateOf(0f),
    val isSelected: MutableState<Boolean> = mutableStateOf(true)
)
@Composable
 fun EditableSticker(
    sticker: StickerItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .graphicsLayer {
                translationX = sticker.offset.value.x
                translationY = sticker.offset.value.y
                scaleX = sticker.scale.floatValue
                scaleY = sticker.scale.floatValue
                rotationZ = sticker.rotation.floatValue
            }
            .size(170.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotate ->
                    onClick()

                    sticker.offset.value += pan
                    sticker.scale.floatValue =
                        (sticker.scale.floatValue * zoom).coerceIn(0.4f, 4f)
                    sticker.rotation.floatValue += rotate
                }
            }
            .clickableNoRipple {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sticker.value,
            fontSize = 80.sp
        )

        if (sticker.isSelected.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = WhiteColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            StickerHandleButton(
                icon = R.drawable.ic_close,
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onDeleteClick
            )

            StickerHandleButton(
                icon = R.drawable.ic_rotate,
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = {
                    sticker.rotation.floatValue += 15f
                }
            )

            StickerHandleButton(
                icon = R.drawable.ic_resize,
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    sticker.scale.floatValue =
                        (sticker.scale.floatValue + 0.1f).coerceIn(0.4f, 4f)
                }
            )
        }
    }
}

@Composable
    fun StickerRotateHandle(
    modifier: Modifier = Modifier,
    onRotate: (Float) -> Unit
) {
    var previousAngle by remember { mutableStateOf<Float?>(null) }

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(PrimaryColor)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        val center = Offset(
                            x = size.width / 2f,
                            y = size.height / 2f
                        )

                        previousAngle = Math.toDegrees(
                            kotlin.math.atan2(
                                startOffset.y - center.y,
                                startOffset.x - center.x
                            ).toDouble()
                        ).toFloat()
                    },
                    onDrag = { change, _ ->
                        val center = Offset(
                            x = size.width / 2f,
                            y = size.height / 2f
                        )

                        val currentAngle = Math.toDegrees(
                            kotlin.math.atan2(
                                change.position.y - center.y,
                                change.position.x - center.x
                            ).toDouble()
                        ).toFloat()

                        val lastAngle = previousAngle ?: currentAngle
                        val delta = currentAngle - lastAngle

                        onRotate(delta)
                        previousAngle = currentAngle

                        change.consume()
                    },
                    onDragEnd = {
                        previousAngle = null
                    },
                    onDragCancel = {
                        previousAngle = null
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_rotate),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun StickerResizeHandle(
    modifier: Modifier = Modifier,
    onDrag: (Offset) -> Unit
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(PrimaryColor)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_resize),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun StickerHandleButton(
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(PrimaryColor)
            .clickableNoRipple {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier.size(16.dp)
        )
    }
}