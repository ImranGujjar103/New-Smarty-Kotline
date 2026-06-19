package com.imr.example.newsmartykotlin.presentation.saved

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun SavedScreen(
    navController: NavController,
    viewModel: SavedViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                SavedEvent.NavigateTryMore -> {
                    navController.navigate(AppRoutes.Gallery.route) {
                        popUpTo(AppRoutes.Gallery.route) {
                            inclusive = true
                        }
                    }
                }

                SavedEvent.NavigateBgRemoverTryMore -> {
                    navController.navigate(AppRoutes.GalleryForBgRemover.createRoute()) {
                        popUpTo(AppRoutes.GalleryForBgRemover.route) {
                            inclusive = true
                        }
                    }
                }

                is SavedEvent.ShareImage -> {
                    shareSavedImage(
                        context = context,
                        imagePath = event.imagePath
                    )
                }
                is SavedEvent.NavigatePassportTryMore -> {
                    navController.navigate(
                        AppRoutes.PassportTryMoreDetail.createRoute(
                            countryId = event.countryId,
                            documentType = event.documentType
                        )
                    ) {
                        popUpTo(AppRoutes.Saved.route) {
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
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        SavedTopBar(
            onHomeClick = {
                navController.navigate(AppRoutes.Home.route) {
                    popUpTo(AppRoutes.Home.route) {
                        inclusive = true
                    }
                }

            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        SavedImageCard(
            imagePath = uiState.imagePath
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.share_to),
            fontFamily = SfProDisplayBold,
            color = TextColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ShareRow(
            onInstagramClick = viewModel::onInstagramClick,
            onFacebookClick = viewModel::onFacebookClick,
            onWhatsAppClick = viewModel::onWhatsAppClick,
            onXClick = viewModel::onXClick,
            onShareClick = viewModel::onShareClick
        )

        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryColor)
                .clickable {
                    viewModel.onTryMoreClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.try_more),
                fontFamily = SfProDisplayBold,
                color = WhiteColor
            )
        }
    }
}

@Composable
private fun SavedTopBar(
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.saved),
            fontFamily = SfProDisplayBold,
            color = TextColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable { onHomeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_saved),
                contentDescription = stringResource(R.string.saved),
                tint = WhiteColor,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun SavedImageCard(
    imagePath: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(WhiteColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(imagePath),
            contentDescription = stringResource(R.string.saved_image),
            modifier = Modifier
                .fillMaxSize()
                ,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ShareRow(
    onInstagramClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onXClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShareIconButton(
            icon = R.drawable.ic_instagram,
            contentDescription = stringResource(R.string.instagram),
            onClick = onInstagramClick
        )

        ShareIconButton(
            icon = R.drawable.ic_facebook,
            contentDescription = stringResource(R.string.facebook),
            onClick = onFacebookClick
        )

        ShareIconButton(
            icon = R.drawable.ic_whatsapp,
            contentDescription = stringResource(R.string.whatsapp),
            onClick = onWhatsAppClick
        )

        ShareIconButton(
            icon = R.drawable.ic_x,
            contentDescription = stringResource(R.string.x),
            onClick = onXClick
        )

        ShareIconButton(
            icon = R.drawable.ic_share,
            contentDescription = stringResource(R.string.share),
            onClick = onShareClick
        )
    }
}

@Composable
private fun ShareIconButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = androidx.compose.ui.graphics.Color.Unspecified,
            modifier = Modifier.size(34.dp)
        )
    }
}

private fun shareSavedImage(
    context: Context,
    imagePath: String
) {

    val uri: Uri = if (imagePath.startsWith("content://")) {
        imagePath.toUri()
    } else {
        val file = File(imagePath)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.share_to)
        )
    )
}