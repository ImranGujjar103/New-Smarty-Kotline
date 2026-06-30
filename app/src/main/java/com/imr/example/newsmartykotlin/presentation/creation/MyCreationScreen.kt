package com.imr.example.newsmartykotlin.presentation.creation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.Creation
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.CardColor
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyCreationScreen(
    navController: NavController,
    viewModel: MyCreationViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.myCreationNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("MyCreationBottomNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd) {
        if (showAd) {
            adViewModel.loadNativeAd(
                adId = config.myCreationNative.adId,
                tag = "MyCreationBottomNative"
            ) { _ -> }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                MyCreationEvent.NavigateBack -> navController.popBackStack()
                is MyCreationEvent.ShareCreation -> {
                    shareCreation(context, event.creation)
                }
            }
        }
    }

    BackHandler {
        viewModel.onBackClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.selectedCreation == null) {
                CreationsGrid(
                    modifier = Modifier.weight(1f),
                    creations = uiState.creations,
                    isLoading = uiState.isLoading,
                    onBackClick = viewModel::onBackClick,
                    onCreationClick = viewModel::onCreationClick
                )
            } else {
                CreationDetail(
                    modifier = Modifier.weight(1f),
                    creation = uiState.selectedCreation!!,
                    onBackClick = viewModel::onBackClick,
                    onDeleteClick = viewModel::onDeleteClick,
                    onShareClick = viewModel::onShareClick,
                    onTryMoreClick = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Home.route) { inclusive = true }
                        }
                    }
                )
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

        if (uiState.showDeleteDialog) {
            DeleteConfirmationDialog(
                onDismiss = viewModel::onDismissDeleteDialog,
                onConfirm = viewModel::onConfirmDelete
            )
        }
    }
}

@Composable
private fun CreationsGrid(
    modifier: Modifier = Modifier,
    creations: List<Creation>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onCreationClick: (Creation) -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(25.dp))

        CreationTopBar(
            title = stringResource(R.string.creation),
            onBackClick = onBackClick,
            showDelete = false
        )
        Spacer(modifier = Modifier.height(7.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (creations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.no_creations_found), color = TextColor)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(creations) { creation ->
                    Image(
                        painter = rememberAsyncImagePainter(creation.uri),
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onCreationClick(creation) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun CreationDetail(
    modifier: Modifier = Modifier,
    creation: Creation,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onTryMoreClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        CreationTopBar(
            title = stringResource(R.string.creation),
            onBackClick = onBackClick,
            showDelete = true,
            onDeleteClick = onDeleteClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(WhiteColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(creation.uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                ,
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.share_to),
            fontFamily = SfProDisplayBold,
            color = TextColor,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        CreationShareRow(
            onShareClick = onShareClick
        )

        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryColor)
                .clickable { onTryMoreClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.try_more),
                fontFamily = SfProDisplayBold,
                color = WhiteColor,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CreationTopBar(
    title: String,
    onBackClick: () -> Unit,
    showDelete: Boolean = false,
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.back),
                tint = WhiteColor,
                modifier = Modifier.size(10.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            fontFamily = SfProDisplayBold,
            fontSize = 18.sp,
            color = TextColor,
            modifier = Modifier.weight(1f)
        )

        if (showDelete) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryColor)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                // Using ic_close as placeholder if ic_delete not found
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.delete),
                    tint = WhiteColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun CreationShareRow(
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShareIcon(R.drawable.ic_instagram, onShareClick)
        ShareIcon(R.drawable.ic_facebook, onShareClick)
        ShareIcon(R.drawable.ic_whatsapp, onShareClick)
        ShareIcon(R.drawable.ic_x, onShareClick)
        ShareIcon(R.drawable.ic_share, onShareClick)
    }
}

@Composable
private fun ShareIcon(iconRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    fontFamily = SfProDisplayBold,
                    fontSize = 20.sp,
                    color = TextColor
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.delete_creation_confirmation),
                    fontSize = 14.sp,
                    color = TextColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardColor)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontFamily = SfProDisplayBold,
                            color = TextColor
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardColor)
                            .clickable { onConfirm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            fontFamily = SfProDisplayBold,
                            color = TextColor
                        )
                    }
                }
            }
        }
    }
}

private fun shareCreation(context: Context, creation: Creation) {
    val uri = creation.uri.toUri()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)))
}
