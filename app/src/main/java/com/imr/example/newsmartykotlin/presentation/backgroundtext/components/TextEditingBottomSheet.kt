package com.imr.example.newsmartykotlin.presentation.backgroundtext.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.util.CollectionUtils.listOf

import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.extensions.clickableNoRipple
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.EditableStickerItem
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.TextColorItem
import com.imr.example.newsmartykotlin.presentation.backgroundtext.model.TextFontOption
import com.imr.example.newsmartykotlin.ui.theme.BlueColor
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.CyanColor
import com.imr.example.newsmartykotlin.ui.theme.GreenColor
import com.imr.example.newsmartykotlin.ui.theme.LightGrayColor
import com.imr.example.newsmartykotlin.ui.theme.MintGreenColor
import com.imr.example.newsmartykotlin.ui.theme.OrangeColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.PurpleColor
import com.imr.example.newsmartykotlin.ui.theme.RedColor_
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.TextFonts
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import com.imr.example.newsmartykotlin.ui.theme.YellowColor




enum class TextEditingTab {
    Fonts,
    Shadow,
    Color,
    Align
}

@Composable
fun TextEditingBottomSheet(
    selectedTextSticker: EditableStickerItem,
    selectedTab: TextEditingTab,
    onTabClick: (TextEditingTab) -> Unit,
    onFontClick: (TextFontOption) -> Unit,
    onColorClick: (Color) -> Unit,
    onShadowClick: (Boolean) -> Unit,
    onAlignClick: (TextAlign) -> Unit,
    onCollapseClick: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(WhiteColor)
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp, bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add Text",
                color = TextColor,
                fontFamily = SfProDisplayBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.ic_close_bottom_sheet),
                contentDescription = null,
                tint = TextColor,
                modifier = Modifier
                    .size(18.dp)
                    .clickableNoRipple {
                        onCollapseClick()
                    }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                TextEditingTab.Fonts -> FontOptionsGrid(
                    selectedFontId = selectedTextSticker.textStyle.fontId,
                    onFontClick = onFontClick
                )

                TextEditingTab.Shadow -> ShadowOptionsRow(
                    shadowEnabled = selectedTextSticker.textStyle.shadowEnabled,
                    onShadowClick = onShadowClick
                )

                TextEditingTab.Color -> ColorOptionsRow(
                    selectedColor = selectedTextSticker.textStyle.color,
                    onColorClick = onColorClick
                )

                TextEditingTab.Align -> AlignOptionsRow(
                    selectedAlign = selectedTextSticker.textStyle.textAlign,
                    onAlignClick = onAlignClick
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextEditingTabItem(
                title = "Fonts",
                isSelected = selectedTab == TextEditingTab.Fonts,
                onClick = { onTabClick(TextEditingTab.Fonts) }
            )

            TextEditingTabItem(
                title = "Shadow",
                isSelected = selectedTab == TextEditingTab.Shadow,
                onClick = { onTabClick(TextEditingTab.Shadow) }
            )

            TextEditingTabItem(
                title = "Color",
                isSelected = selectedTab == TextEditingTab.Color,
                onClick = { onTabClick(TextEditingTab.Color) }
            )

            TextEditingTabItem(
                title = "Align",
                isSelected = selectedTab == TextEditingTab.Align,
                onClick = { onTabClick(TextEditingTab.Align) }
            )
        }
    }
}

@Composable
private fun FontOptionsGrid(
    selectedFontId: String,
    onFontClick: (TextFontOption) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        userScrollEnabled = true,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    ) {
        items(TextFonts) { font ->
            val isSelected = selectedFontId == font.id

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) WhiteColor else CardColor)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryColor else Color.Transparent,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickableNoRipple {
                        onFontClick(font)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = font.title,
                    fontSize = 12.sp,
                    color = if (isSelected) PrimaryColor else TextColor,
                    fontFamily = font.fontFamily,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ColorOptionsRow(
    selectedColor: Color,
    onColorClick: (Color) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val controller = rememberColorPickerController()

    val colors: List<TextColorItem> = listOf(
        TextColorItem.CustomPicker,
        TextColorItem.Preset(LightGrayColor),
        TextColorItem.Preset(RedColor_),
        TextColorItem.Preset(OrangeColor),
        TextColorItem.Preset(YellowColor),
        TextColorItem.Preset(GreenColor),
        TextColorItem.Preset(MintGreenColor),
        TextColorItem.Preset(CyanColor),
        TextColorItem.Preset(BlueColor),
        TextColorItem.Preset(PurpleColor)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(colors) { item ->
            when (item) {
                TextColorItem.CustomPicker -> {
                    Image(
                        painter = painterResource(R.drawable.ic_color_picker),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clickableNoRipple {
                                showColorPicker = true
                            }
                    )
                }

                is TextColorItem.Preset -> {
                    val isSelected = selectedColor == item.color

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) PrimaryColor else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(item.color)
                            .clickableNoRipple {
                                onColorClick(item.color)
                            }
                    )
                }
            }
        }
    }

    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = {
                Text(
                    text = "Choose Color",
                    fontFamily = SfProDisplayBold,
                    color = TextColor
                )
            },
            text = {
                Color(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        onColorClick(colorEnvelope.color)
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showColorPicker = false }
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
}

@Composable
private fun ShadowOptionsRow(
    shadowEnabled: Boolean,
    onShadowClick: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShadowOptionButton(
            title = "No Shadow",
            isSelected = !shadowEnabled,
            onClick = { onShadowClick(false) }
        )

        ShadowOptionButton(
            title = "Shadow",
            isSelected = shadowEnabled,
            onClick = { onShadowClick(true) }
        )
    }
}

@Composable
private fun ShadowOptionButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 130.dp, height = 38.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) WhiteColor else CardColor)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryColor else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .clickableNoRipple {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontFamily = SfProDisplayBold,
            color = if (isSelected) PrimaryColor else TextColor
        )
    }
}

@Composable
private fun AlignOptionsRow(
    selectedAlign: TextAlign,
    onAlignClick: (TextAlign) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AlignOptionButton(
            title = "Left",
            isSelected = selectedAlign == TextAlign.Start,
            onClick = { onAlignClick(TextAlign.Start) }
        )

        AlignOptionButton(
            title = "Center",
            isSelected = selectedAlign == TextAlign.Center,
            onClick = { onAlignClick(TextAlign.Center) }
        )

        AlignOptionButton(
            title = "Right",
            isSelected = selectedAlign == TextAlign.End,
            onClick = { onAlignClick(TextAlign.End) }
        )
    }
}

@Composable
private fun AlignOptionButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 90.dp, height = 38.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) WhiteColor else CardColor)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryColor else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .clickableNoRipple {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontFamily = SfProDisplayBold,
            color = if (isSelected) PrimaryColor else TextColor
        )
    }
}

@Composable
private fun TextEditingTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontFamily = SfProDisplayBold,
        color = if (isSelected) PrimaryColor else TextColor,
        modifier = Modifier.clickableNoRipple {
            onClick()
        }
    )
}