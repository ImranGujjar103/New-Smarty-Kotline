package com.imr.example.newsmartykotlin.presentation.photoeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoEditorScreen(
    navController: NavController,
    viewModel: PhotoEditorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PhotoEditorTopBar(
            onBackClick = { navController.popBackStack() },
            onNextClick = { }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.suitItem != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.suitItem!!.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(520.dp),
                        contentScale = ContentScale.Fit
                    )

                    Image(
                        painter = rememberAsyncImagePainter(uiState.faceImageUri.toUri()),
                        contentDescription = null,
                        modifier = Modifier
                            .width(122.dp)
                            .height(150.dp)
                            .offset(y = (-62).dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            FloatingSuitButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 28.dp)
            )
        }

        EditorBottomBar()
    }
}

@Composable
private fun PhotoEditorTopBar(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .background(
                Brush.verticalGradient(
                    listOf(WhiteColor, CardColor)
                )
            )
            .padding(horizontal = 28.dp)
            .padding(top = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onBackClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "‹",
                    color = WhiteColor,
                    style = AppTypography.Title
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Photo Editor",
            style = AppTypography.Title,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onNextClick,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            ),
            modifier = Modifier.height(42.dp)
        ) {
            Text(
                text = "Next",
                color = WhiteColor,
                style = AppTypography.Body
            )
        }
    }
}

@Composable
private fun FloatingSuitButton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👔",
            color = WhiteColor,
            style = AppTypography.Title
        )
    }
}

@Composable
private fun EditorBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(WhiteColor)
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorBottomItem("👔", "Outfits")
        EditorBottomItem("🩹", "Eraser")
        EditorBottomItem("◯", "Face Flip")
        EditorBottomItem("👔", "Suit Flip")
    }
}

@Composable
private fun EditorBottomItem(
    icon: String,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = AppTypography.Body,
            color = SubTextColor
        )
    }
}