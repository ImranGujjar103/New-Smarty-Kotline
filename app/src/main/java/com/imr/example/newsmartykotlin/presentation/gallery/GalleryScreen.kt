package com.imr.example.newsmartykotlin.presentation.gallery

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.GalleryImage
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.AppTypography
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.RedColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SubTextColor
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryScreen(
    navController: NavController,
    viewModel: GalleryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                GalleryEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadGalleryImages()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        GalleryTopBar(
            onBackClick = viewModel::onBackClick
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = WhiteColor,
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp
            )
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "Something went wrong",
                            style = AppTypography.Body,
                            color = RedColor
                        )
                    }
                }

                uiState.filteredImages.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        FolderTabsRow(
                            folders = uiState.folders,
                            selectedFolder = uiState.selectedFolderName,
                            onFolderClick = viewModel::onFolderClick
                        )

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No images found.",
                                style = AppTypography.Body,
                                color = SubTextColor
                            )
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        FolderTabsRow(
                            folders = uiState.folders,
                            selectedFolder = uiState.selectedFolderName,
                            onFolderClick = viewModel::onFolderClick
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 22.dp),
                            contentPadding = PaddingValues(
                                top = 14.dp,
                                bottom = 90.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.filteredImages,
                                key = { it.id }
                            ) { image ->
                                GalleryImageItem(
                                    image = image,
                                    onImageClick = {

                                        selectedImage ->

                                        Log.d(
                                            "GallerySuitItem",
                                            "Suit Id 33333 = ${viewModel.suitId}"
                                        )

                                        navController.navigate(
                                            AppRoutes.CropFace.createRoute(
                                                suitId = viewModel.suitId,
                                                imageUri = selectedImage.uri
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderTabsRow(
    folders: List<String>,
    selectedFolder: String,
    onFolderClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .background(WhiteColor)
            .padding(
                horizontal = 10.dp,
                vertical = 10.dp
            )
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        folders.forEach { folder ->
            val selected = folder == selectedFolder

            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) {
                            PrimaryColor
                        } else {
                            CardColor
                        }
                    )
                    .clickable {
                        onFolderClick(folder)
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = folder,
                    fontFamily = SfProDisplayBold,
                    color = if (selected) WhiteColor else TextColor,
                    maxLines = 1,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GalleryTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                CardColor
            )
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable(true, onClick = onBackClick)
            ,
            contentAlignment = Alignment.Center
        ) {
            Icon(modifier = Modifier.size(10.dp),
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                tint = WhiteColor
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = stringResource(R.string.select_image),
            fontSize = 18.sp,
            fontFamily = SfProDisplayBold,
            color = TextColor
        )
    }
}

@Composable
private fun GalleryImageItem(
    image: GalleryImage,
    onImageClick: (GalleryImage) -> Unit
) {
    AsyncImage(
        model = image.uri.toUri(),
        contentDescription = image.fileName,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                onImageClick(image)
            },
        contentScale = ContentScale.Crop
    )
}