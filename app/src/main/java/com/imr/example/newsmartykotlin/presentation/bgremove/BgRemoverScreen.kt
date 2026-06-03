package com.imr.example.newsmartykotlin.presentation.bgremove

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.AppTypography
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.RedColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun BgRemoveScreen(
    navController: NavController,
    viewModel: BgRemoveViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BgRemoveEvent.NavigateNext -> {
                    navController.navigate(
                        AppRoutes.PhotoEditor.createRoute(
                            suitUrl = event.suitId,
                            croppedImageUri = event.removedBgImageUri
                        )
                    ) {
                        popUpTo(AppRoutes.BgRemove.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(480.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(WhiteColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(uiState.croppedImageUri.toUri()),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = uiState.progressText,
            fontSize = 24.sp,
            fontFamily = SfProDisplayBold,
            color = TextColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier
                .width(250.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(50.dp)),
            color = PrimaryColor,
            trackColor = WhiteColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.removing_background),
            fontSize = 12.sp,
            fontFamily = SfProDisplayBold,
            color = TextColor
        )

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = error,
                fontSize = 12.sp,
                fontFamily = SfProDisplayBold,
                color = RedColor
            )
        }
    }
}