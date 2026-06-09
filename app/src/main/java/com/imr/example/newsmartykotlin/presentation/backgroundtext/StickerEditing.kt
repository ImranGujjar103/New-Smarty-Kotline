package com.imr.example.newsmartykotlin.presentation.backgroundtext

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.extensions.clickableNoRipple
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

data class StickerItem(
    val id: Long,
    val value: String,
    val isEmoji: Boolean = true,
    val offset: MutableState<Offset> = mutableStateOf(Offset.Zero),
    val scale: MutableFloatState = mutableFloatStateOf(1f),
    val rotation: MutableFloatState = mutableFloatStateOf(0f),
    val isSelected: MutableState<Boolean> = mutableStateOf(true)
)

@Composable
fun EditableSticker(
    sticker: StickerItem,
    onClick: () -> Unit,
    onSelect: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val stickerBoxSizePx = remember { mutableFloatStateOf(0f) }

    val isSelected = sticker.isSelected.value
    val isEmoji = sticker.isEmoji
    Box(
        modifier = if (isEmoji){
            Modifier
                .zIndex(if (isSelected) 10f else 0f)
                .graphicsLayer {
                    transformOrigin = TransformOrigin.Center
                    translationX = sticker.offset.value.x
                    translationY = sticker.offset.value.y
                    scaleX = sticker.scale.floatValue
                    scaleY = sticker.scale.floatValue
                    rotationZ = sticker.rotation.floatValue
                }
                .size(120.dp)
                .pointerInput(sticker.id, isSelected) {
                    if (isSelected) {
                        detectTransformGestures { _, pan, zoom, rotate ->
                            onSelect()

                            sticker.offset.value += pan
                            sticker.scale.floatValue =
                                (sticker.scale.floatValue * zoom).coerceIn(0.2f, 4f)
                            sticker.rotation.floatValue += rotate
                        }
                    } else {
                        detectTapGestures(
                            onTap = {
                                onSelect()
                            }
                        )
                    }
                }
        }else{
            Modifier
                .zIndex(if (isSelected) 10f else 0f)
                .graphicsLayer {
                    transformOrigin = TransformOrigin.Center
                    translationX = sticker.offset.value.x
                    translationY = sticker.offset.value.y
                    scaleX = sticker.scale.floatValue
                    scaleY = sticker.scale.floatValue
                    rotationZ = sticker.rotation.floatValue
                }
                .defaultMinSize(
                    minWidth = 150.dp,
                    minHeight = 90.dp
                )
                .padding(horizontal = 30.dp, vertical = 6.dp)
                .pointerInput(sticker.id, isSelected) {
                    if (isSelected) {
                        detectTransformGestures { _, pan, zoom, rotate ->
                            onSelect()

                            sticker.offset.value += pan
                            sticker.scale.floatValue =
                                (sticker.scale.floatValue * zoom).coerceIn(0.2f, 4f)
                            sticker.rotation.floatValue += rotate
                        }
                    } else {
                        detectTapGestures(
                            onTap = {
                                onSelect()
                            }
                        )
                    }
                }
        },
        contentAlignment = Alignment.Center
    ) {


        Text(
            modifier = Modifier.padding(horizontal = if (isEmoji)0.dp else 20.dp),
            text = sticker.value,
            fontSize = if (isEmoji) 60.sp else 42.sp,
            color = if (isEmoji) Color.Unspecified else WhiteColor,
            fontFamily = if (isEmoji) null else SfProDisplayBold
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = 2.dp,
                        color = WhiteColor,
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            DeleteHandle(
                modifier = Modifier.align(Alignment.TopEnd)
                    .offset(
                        x = 22.dp,
                        y = (-22).dp
                    ),
                onDeleteClick = onDeleteClick
            )

            RotateHandle(
                modifier = Modifier.align(Alignment.BottomStart)
                    .zIndex(10f)
                    .offset(
                        x = (-13).dp,
                        y = (13).dp
                    ),
                rotation = sticker.rotation,
                onTouch = onClick
            )

            ResizeHandle(
                modifier = Modifier.align(Alignment.BottomEnd)
                    .zIndex(10f)                    .offset(
                        x = (13).dp,
                        y = (13).dp
                    ),
                scale = sticker.scale,
                onTouch = onClick
            )
        }
    }
}

@Composable
private fun DeleteHandle(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = modifier
            .zIndex(10f)
            .size(44.dp)
            .clickableNoRipple {
                onDeleteClick()
            },
        contentAlignment = Alignment.Center
    ) {
        StickerHandleButton(
            icon = R.drawable.ic_close
        )
    }
}

@Composable
private fun ResizeHandle(
    modifier: Modifier = Modifier,
    scale: MutableFloatState,
    onTouch: () -> Unit
) {

    StickerHandleButton(
        icon = R.drawable.ic_resize,
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { localTouch ->
                    onTouch()

                },
                onDrag = { change, dragAmount ->

                    val delta =
                        (dragAmount.x + dragAmount.y) * 0.003f

                    scale.floatValue =
                        (scale.floatValue + delta)
                            .coerceIn(0.4f, 4f)

                    change.consume()
                },
                onDragEnd = {

                },
                onDragCancel = {

                }
            )
        }
    )
}

@Composable
private fun RotateHandle(
    modifier: Modifier = Modifier,
    rotation: MutableFloatState,
    onTouch: () -> Unit
) {
    StickerHandleButton(
        icon = R.drawable.ic_rotate,
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    onTouch()
                },
                onDrag = { change, dragAmount ->

                    val delta =
                        (dragAmount.x - dragAmount.y) * 0.45f

                    rotation.floatValue += delta

                    change.consume()
                }
            )
        }
    )
}

@Composable
private fun StickerHandleButton(
    icon: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = WhiteColor,
            modifier = Modifier.size(14.dp).rotate(90f)
        )
    }
}
