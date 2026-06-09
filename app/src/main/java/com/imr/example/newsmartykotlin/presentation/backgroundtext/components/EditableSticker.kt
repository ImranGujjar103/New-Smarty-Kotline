package com.imr.example.newsmartykotlin.presentation.backgroundtext.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.extensions.clickableNoRipple
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.EditableStickerItem
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.TextFontOption
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextFonts
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun EditableSticker(
    sticker: EditableStickerItem,
    onSelect: () -> Unit,
    onDeleteClick: () -> Unit,
    onTransform: (pan: Offset, zoom: Float, rotation: Float) -> Unit,
    onResizeDrag: (Float) -> Unit,
    onRotateDrag: (Float) -> Unit
) {
    val isSelected = sticker.isSelected
    val isEmoji = sticker.isEmoji

    Box(
        modifier = Modifier
            .zIndex(if (isSelected) 10f else 0f)
            .graphicsLayer {
                transformOrigin = TransformOrigin.Center
                translationX = sticker.offset.x
                translationY = sticker.offset.y
                scaleX = sticker.scale
                scaleY = sticker.scale
                rotationZ = sticker.rotation
            }
            .then(
                if (isEmoji) {
                    Modifier.size(120.dp)
                } else {
                    Modifier
                        .defaultMinSize(
                            minWidth = 150.dp,
                            minHeight = 70.dp
                        )
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                }
            )
            .pointerInput(sticker.id, isSelected) {
                if (isSelected) {
                    detectTransformGestures { _, pan, zoom, rotate ->
                        onSelect()
                        onTransform(pan, zoom, rotate)
                    }
                } else {
                    detectTapGestures(
                        onTap = {
                            onSelect()
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isEmoji) {
            Text(
                text = sticker.value,
                fontSize = 60.sp,
                color = Color.Unspecified
            )
        } else {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = sticker.value,
                fontSize = sticker.textStyle.fontSize.sp,
                color = sticker.textStyle.color,
                fontFamily = getFontFamily(sticker.textStyle.fontId),
                textAlign = sticker.textStyle.textAlign,
                maxLines = 2,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = if (sticker.textStyle.shadowEnabled) {
                        Shadow(
                            color = Color.Black.copy(alpha = 0.75f),
                            offset = Offset(3f, 3f),
                            blurRadius = 5f
                        )
                    } else {
                        null
                    }
                )
            )
        }

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
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 22.dp, y = (-22).dp)
                    .zIndex(20f),
                onDeleteClick = onDeleteClick
            )

            RotateHandle(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-13).dp, y = 13.dp)
                    .zIndex(20f),
                onRotateDrag = onRotateDrag,
                onTouch = onSelect
            )

            ResizeHandle(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 13.dp, y = 13.dp)
                    .zIndex(20f),
                onResizeDrag = onResizeDrag,
                onTouch = onSelect
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
    onResizeDrag: (Float) -> Unit,
    onTouch: () -> Unit
) {
    StickerHandleButton(
        icon = R.drawable.ic_resize,
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    onTouch()
                },
                onDrag = { change, dragAmount ->
                    val delta = (dragAmount.x + dragAmount.y) * 0.003f
                    onResizeDrag(delta)
                    change.consume()
                }
            )
        }
    )
}

@Composable
private fun RotateHandle(
    modifier: Modifier = Modifier,
    onRotateDrag: (Float) -> Unit,
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
                    val delta = (dragAmount.x - dragAmount.y) * 0.45f
                    onRotateDrag(delta)
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
            modifier = Modifier
                .size(14.dp)
                .rotate(90f)
        )
    }
}

@Composable
fun getFontFamily(fontId: String): FontFamily {
    return TextFonts.firstOrNull {
        it.id == fontId
    }?.fontFamily ?: SfProDisplayBold
}