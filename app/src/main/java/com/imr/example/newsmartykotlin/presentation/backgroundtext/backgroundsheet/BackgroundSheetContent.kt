package com.imr.example.newsmartykotlin.presentation.backgroundtext.backgroundsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.BackgroundData
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.DisabledTextColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import kotlinx.coroutines.launch

@Composable
fun BackgroundSheetContent(
    uiState: BackgroundUiState,
    selectedBackgroundPath: String,
    onRetryClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onBackgroundClick: (BackgroundData) -> Unit,
    onCloseClick: () -> Unit
) {
    var selectedCategoryIndex by remember(uiState.categories, selectedBackgroundPath) {
        val index = uiState.categories.indexOfFirst { section ->
            section.backgrounds.any { it.imageUrl == selectedBackgroundPath }
        }
        mutableIntStateOf(if (index != -1) index else 0)
    }

    val backgroundListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedCategoryIndex, selectedBackgroundPath) {
        val backgrounds = uiState.categories.getOrNull(selectedCategoryIndex)?.backgrounds.orEmpty()
        val index = backgrounds.indexOfFirst { it.imageUrl == selectedBackgroundPath }

        if (index != -1) {
            val scrollIndex = if (selectedCategoryIndex == 0) index + 2 else index
            backgroundListState.scrollToItem(scrollIndex)
        } else if (selectedCategoryIndex == 0 && selectedBackgroundPath.isEmpty()) {
            backgroundListState.scrollToItem(1) // Transparent item
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
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

            IconButton(onClick = onCloseClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_bottom_sheet),
                    contentDescription = stringResource(R.string.close)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when {
            uiState.isLoading -> LoadingContent()

            uiState.error != null -> {
                ErrorContent(
                    message = uiState.error,
                    onRetryClick = onRetryClick
                )
            }

            uiState.categories.isEmpty() -> EmptyContent()

            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 20.dp)
                ) {
                    itemsIndexed(uiState.categories) { index, category ->
                        CategoryChip(
                            title = category.categoryName,
                            selected = selectedCategoryIndex == index,
                            onClick = {
                                selectedCategoryIndex = index

                                scope.launch {
                                    backgroundListState.scrollToItem(0)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val backgrounds = uiState.categories
                    .getOrNull(selectedCategoryIndex)
                    ?.backgrounds
                    .orEmpty()

                val isAllSelected = selectedCategoryIndex == 0

                LazyRow(
                    state = backgroundListState,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 24.dp)
                ) {
                    if (isAllSelected) {
                        item {
                            GalleryBackgroundItem(
                                onClick = {
                                    onGalleryClick()
                                }
                            )
                        }

                        item {
                            TransparentBackgroundItem(
                                selected = selectedBackgroundPath.isEmpty(),
                                onClick = {
                                    onBackgroundClick(BackgroundData("",""))
                                }
                            )
                        }
                    }

                    itemsIndexed(
                        items = backgrounds,
                        key = { _, background -> background.imageUrl }
                    ) { _, background ->
                        BackgroundItem(
                            background = background,
                            selected = background.imageUrl == selectedBackgroundPath,
                            onClick = {
                                onBackgroundClick(background)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Color(0xFF5B8DEF)
                else Color(0xFFF1F1F1)
            )
            .clickable { onClick() }
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color(0xFF2E2E2E),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun BackgroundItem(
    background: BackgroundData,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFEFEFEF))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = background.imageUrl,
            contentDescription = background.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_transparent_tick),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun GalleryBackgroundItem(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_gallery),
            contentDescription = null,
            tint = DisabledTextColor,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.gallery),
            fontWeight = FontWeight.Bold,
            fontFamily = SfProDisplayBold,
            fontSize = 10.sp,
            color = TextColor
        )
    }
}

@Composable
fun TransparentBackgroundItem(
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.bg_transparent),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (selected) {
            Image(
                painter = painterResource(R.drawable.ic_transparent_tick),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetryClick) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.no_backgrounds_found))
    }
}