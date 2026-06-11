package com.imr.example.newsmartykotlin.presentation.bgremovereditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet.BackgroundBottomSheet
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*

import com.imr.example.newsmartykotlin.presentation.navigation.SELECTED_BACKGROUND_IMAGE_KEY


@Composable
fun BgRemoverEditorScreen(
    navController: NavController,
    viewModel: BgRemoverEditorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val selectedBackgroundImage =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<String?>(SELECTED_BACKGROUND_IMAGE_KEY, null)
            ?.collectAsState()

    LaunchedEffect(selectedBackgroundImage?.value) {
        selectedBackgroundImage?.value?.let { uri ->
            viewModel.updateBackground(uri)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>(SELECTED_BACKGROUND_IMAGE_KEY)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                BgRemoverEditorEvent.Back -> navController.popBackStack()

                is BgRemoverEditorEvent.Saved -> {
                    navController.navigate(AppRoutes.Saved.createRoute(event.imagePath))
                }

                is BgRemoverEditorEvent.Error -> Unit
            }
        }
    }

    BgRemoverEditorContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackgroundSelected = viewModel::updateBackground,
        onGalleryClick = {
            navController.navigate(AppRoutes.GalleryForBackground.route)
        }
    )
}

@Composable
private fun BgRemoverEditorContent(
    uiState: BgRemoverEditorUiState,
    onAction: (BgRemoverEditorAction) -> Unit,
    onBackgroundSelected: (String) -> Unit,
    onGalleryClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var showBackgroundSheet by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(if (isDark) Color(0xFF111827) else Color(0xFFEFF3FB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            EditorTopBar(
                onBackClick = {
                    onAction(BgRemoverEditorAction.BackClick)
                },
                onSaveClick = {
                    onAction(BgRemoverEditorAction.SaveClick)
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                EditorPreview(
                    uiState = uiState,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.backgrounds),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            showBackgroundSheet = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close_bottom_sheet),
                            contentDescription = null,
                            modifier = Modifier.rotate(180f)
                        )
                    }
                }
            }

            if (showBackgroundSheet) {
                BackgroundBottomSheet(
                    onDismiss = {
                        showBackgroundSheet = false
                    },
                    onBackgroundSelected = { backgroundUrl ->
                        onBackgroundSelected(backgroundUrl)
                        showBackgroundSheet = false
                    },
                    onGalleryClick = {
                        showBackgroundSheet = false
                        onGalleryClick()
                    }
                )
            }
        }

        if (uiState.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        }
    }
}

@Composable
private fun EditorTopBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable(true,onClick = onBackClick),
            contentAlignment = Alignment.Center

        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = stringResource(R.string.editor),
            fontFamily = SfProDisplayBold,
            color = TextColor,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryColor)
                .clickable(true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_flip),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryColor)
                .clickable(true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_eraser),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .background(shape = RoundedCornerShape(12.dp), color = PrimaryColor)
                .height(30.dp)
                .width(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(true, onClick = onSaveClick),
            contentAlignment = Alignment.Center


        ) {
            Text(
                text = stringResource(R.string.save),
                color = Color.White,
                fontFamily = SfProDisplayBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EditorPreview(
    uiState: BgRemoverEditorUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.82f)
            .aspectRatio(0.75f),
        contentAlignment = Alignment.Center
    ) {
        when (val bg = uiState.selectedBackground) {
            BgEditorBackground.Gallery,
            BgEditorBackground.Transparent -> Unit

            is BgEditorBackground.GalleryImage -> {
                Image(
                    painter = rememberAsyncImagePainter(bg.imageUri),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }

            is BgEditorBackground.ColorBackground -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(bg.color))
                )
            }

            is BgEditorBackground.DrawableBackground -> {
                Image(
                    painter = rememberAsyncImagePainter(bg.resId),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Image(
            painter = rememberAsyncImagePainter(uiState.removedImageUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

