package com.imr.example.newsmartykotlin.presentation.backgroundtext.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.extensions.clickableNoRipple
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor

@Composable
fun StickerBottomSheet(
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
                    .clickableNoRipple {
                        onCloseClick()
                    }
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