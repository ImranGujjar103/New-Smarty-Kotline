package com.imr.example.newsmartykotlin.presentation.saved

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun SavedScreen(
    navController: NavController,
    viewModel: SavedViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.savedNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("SavedNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.savedNative.adId,
                tag = "SavedNative"
            ) { _ -> }
        }
    }

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

                SavedEvent.NavigateSuitTryMore -> {
                    navController.navigate(AppRoutes.Suits.route) {
                        popUpTo(AppRoutes.Suits.route) {
                            inclusive = true
                        }
                    }
                }

                is SavedEvent.ShareImage -> {
                    shareSavedImage(
                        context = context,
                        imagePath = event.imagePath,
                        packageName = event.packageName,
                        platformName = event.platformName
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
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                imagePath = uiState.imagePath,
                modifier = Modifier.weight(1f)
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
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Home.route) {
                                inclusive = true
                            }
                        }
                        //  viewModel.onTryMoreClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.try_more),
                    fontFamily = SfProDisplayBold,
                    color = WhiteColor
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showAd) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                LanguageBottomNativeAd(
                    state = nativeState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
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
    imagePath: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(WhiteColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(imagePath),
            contentDescription = stringResource(R.string.saved_image),
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
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
    imagePath: String,
    packageName: String? = null,
    platformName: String? = null
) {
    if (packageName != null) {
        if (!isAppInstalled(context, packageName)) {
            Toast.makeText(
                context,
                context.getString(R.string.app_not_installed, platformName ?: ""),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
    }

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
        if (packageName != null) {
            setPackage(packageName)
        }
    }

    if (packageName != null) {
        context.startActivity(intent)
    } else {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.share_to)
            )
        )
    }
}

private fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: Exception) {
        false
    }
}
