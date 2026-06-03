package com.imr.example.newsmartykotlin.presentation.suits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.SuitItem
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor

@Composable
fun SuitScreen(
    state: SuitUiState,
    onBackClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    onSuitClick: (SuitItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        CardColor,
                        CardColor
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            SuitTopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(22.dp))

            CategoryTabs(
                state = state,
                onCategoryClick = onCategoryClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                SuitGrid(
                    items = state.selectedItems,
                    onSuitClick = onSuitClick
                )
            }
        }
    }
}

@Composable
private fun SuitTopBar(
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onBackClick()
                },

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
            text = stringResource(R.string.suits),
            fontFamily = SfProDisplayBold,
            fontSize = 22.sp,
            color = TextColor
        )
    }
}

@Composable
private fun CategoryTabs(
    state: SuitUiState,
    onCategoryClick: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(state.categories) { index, category ->
            val selected = index == state.selectedCategoryIndex

            Box(
                modifier = Modifier
                    .height(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selected) PrimaryColor else WhiteColor
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onCategoryClick(index)
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.title,
                    fontFamily = SfProDisplayBold,
                    fontSize = 12.sp,
                    color = if (selected) WhiteColor else TextColor
                )
            }
        }
    }
}

@Composable
private fun SuitGrid(
    items: List<SuitItem>,
    onSuitClick: (SuitItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            AsyncImage(
                model = item.suitUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(149.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WhiteColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onSuitClick(item)
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}