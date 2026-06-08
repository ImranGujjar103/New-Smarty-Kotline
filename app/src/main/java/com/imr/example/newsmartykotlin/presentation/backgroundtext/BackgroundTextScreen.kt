package com.imr.example.newsmartykotlin.presentation.backgroundtext

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.SubTextColor
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundTextScreen(
    navController: NavController,
    viewModel: BackgroundTextViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val captureLayer = rememberGraphicsLayer()

    var selectedStickerId by remember { mutableStateOf<Long?>(null) }
    var showStickerSheet by remember { mutableStateOf(false) }
    val stickers = remember { mutableStateListOf<StickerItem>() }

    var showAddTextDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BackgroundTextEvent.Done -> {
                    // navController.navigate(AppRoutes.FinalPreview.createRoute(event.imagePath))
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
        BackgroundTextTopBar(
            isSaving = uiState.isSaving,
            onBackClick = { navController.popBackStack() },
            onDoneClick = {
                scope.launch {
                    stickers.forEach { it.isSelected.value = false }

                    val bitmap = captureLayer
                        .toImageBitmap()
                        .asAndroidBitmap()

                    viewModel.onDoneClick(bitmap)
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(WhiteColor),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WhiteColor)
                    .drawWithContent {
                        captureLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(captureLayer)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(uiState.imagePath),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 28.dp),
                    contentScale = ContentScale.Fit
                )

                stickers.forEach { sticker ->
                    EditableSticker(
                        sticker = sticker,
                        onClick = {
                            stickers.forEach {
                                it.isSelected.value = false
                            }

                            sticker.isSelected.value = true
                            selectedStickerId = sticker.id
                        },
                        onDeleteClick = {
                            stickers.remove(sticker)
                            selectedStickerId = null
                        }
                    )
                }
            }
        }

        BackgroundTextBottomBar(
            onBackgroundClick = {},
            onTextClick = {
                showAddTextDialog = true
            },
            onStickerClick = {
                showStickerSheet = true
            }
        )
    }

    if (showStickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStickerSheet = false },
            containerColor = WhiteColor,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
        ) {
            StickerBottomSheet(
                onStickerClick = { emoji ->
                    val id = System.currentTimeMillis()

                    stickers.forEach {
                        it.isSelected.value = false
                    }

                    stickers.add(
                        StickerItem(
                            id = id,
                            value = emoji,
                            isSelected = mutableStateOf(true)
                        )
                    )

                    selectedStickerId = id
                    showStickerSheet = false
                },
                onCloseClick = {
                    showStickerSheet = false
                }
            )
        }
    }

    if (showAddTextDialog) {
        AddTextDialog(
            value = inputText,
            onValueChange = { inputText = it },
            onCancelClick = {
                showAddTextDialog = false
                inputText = ""
            },
            onAllowClick = {
                showAddTextDialog = false
                inputText = ""
            }
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

@Composable
private fun StickerBottomSheet(
    onStickerClick: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    val stickerList = listOf(
        "👾", "😠", "😟", "😮", "🐱", "😂", "😼", "🤡", "😖", "😐",
        "😿", "😭", "😢", "😞", "😵", "☺️", "😑", "👽", "😋", "😱",
        "😘", "😓", "🤠", "🤫", "😇", "😤", "😷", "🧐", "🤨", "😯",
        "😨", "🤢", "😶", "🙄", "😛", "😝", "😜", "😂", "🤣", "😰",
        "😳", "😦", "👻", "😁", "😺", "🙂", "🤪", "😄", "🤩", "🤗",
        "😯", "😈", "🎃", "👺", "👹", "😽", "😶", "😏", "🤭", "😭",
        "😟", "🤑", "🤢", "🤓", "😐", "😔", "😣", "💩", "😾", "😡"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
            .padding(horizontal = 18.dp)
            .padding(bottom = 10.dp, top = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.stickers),
                fontFamily = SfProDisplayBold,
                fontSize = 14.sp,
                color = TextColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.ic_close_bottom_sheet),
                contentDescription = null,
                tint = TextColor,
                modifier = Modifier
                    .size(12.dp)
                    .clickableNoRipple { onCloseClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(stickerList) { sticker ->
                Text(
                    text = sticker,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .size(28.dp)
                        .clickableNoRipple {
                            onStickerClick(sticker)
                        }
                )
            }
        }
    }
}


@Composable
private fun BackgroundTextTopBar(
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
                .clickableNoRipple { onBackClick() },
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
            text = stringResource(R.string.background_and_text),
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onDoneClick,
            enabled = !isSaving,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(64.dp)
                .height(32.dp)
        ) {
            Text(
                text = if (isSaving) "..." else stringResource(R.string.save),
                color = WhiteColor,
                fontFamily = SfProDisplayBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun BackgroundTextBottomBar(
    onBackgroundClick: () -> Unit,
    onTextClick: () -> Unit,
    onStickerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(WhiteColor)
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackgroundTextBottomItem(
            icon = R.drawable.ic_background,
            title = stringResource(R.string.background),
            onClick = onBackgroundClick
        )

        BackgroundTextBottomItem(
            icon = R.drawable.ic_add_text,
            title = stringResource(R.string.add_text),
            onClick = onTextClick
        )

        BackgroundTextBottomItem(
            icon = R.drawable.ic_stickers,
            title = stringResource(R.string.stickers),
            onClick = onStickerClick
        )
    }
}

@Composable
private fun BackgroundTextBottomItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickableNoRipple { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = TextColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = title,
            fontFamily = SfProDisplayRegular,
            fontSize = 12.sp,
            color = SubTextColor
        )
    }
}

@Composable
fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier {
    return clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}