package com.imr.example.newsmartykotlin.presentation.settings

import android.content.Intent
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.core.extensions.clickableNoRipple
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.language.components.LanguageBottomNativeAd
import com.imr.example.newsmartykotlin.presentation.navigation.AppRoutes
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import com.imr.example.newsmartykotlin.ui.theme.HomeBackgroundColor
import com.imr.example.newsmartykotlin.ui.theme.PrimaryColor
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayBold
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayMedium
import com.imr.example.newsmartykotlin.ui.theme.SfProDisplayRegular
import com.imr.example.newsmartykotlin.ui.theme.TextColor
import com.imr.example.newsmartykotlin.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
    adViewModel: AdViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val isPurchased by adViewModel.dataStorePrefs.getIsPurchased().collectAsStateWithLifecycle(initialValue = false)
    val isConnected by adViewModel.isConnected.collectAsStateWithLifecycle(initialValue = true)
    val config by adViewModel.adRepository.appConfig.collectAsStateWithLifecycle()

    val showAd = config.settingsNative.toShow && !isPurchased && isConnected

    val nativeState by adViewModel.getNativeAdState("SettingsNative").collectAsStateWithLifecycle()

    LaunchedEffect(showAd, nativeState) {
        if (showAd && (nativeState is LanguageNativeState.Idle || nativeState is LanguageNativeState.Failed)) {
            adViewModel.loadNativeAd(
                adId = config.settingsNative.adId,
                tag = "SettingsNative"
            ) { _ -> }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                SettingsEvent.NavigateBack -> navController.popBackStack()
                SettingsEvent.NavigateToLanguage -> {
                    navController.navigate(AppRoutes.Language.createRoute(false))
                }
                SettingsEvent.NavigateToPremium -> {
                    navController.navigate(AppRoutes.Premium.route)
                }
                SettingsEvent.PrivacyPolicy -> {
                    val intent = Intent(Intent.ACTION_VIEW, "https://google.com".toUri())
                    context.startActivity(intent)
                }
                SettingsEvent.RateApp -> {
                    val intent = Intent(Intent.ACTION_VIEW,
                        "market://details?id=${context.packageName}".toUri())
                    context.startActivity(intent)
                }
                SettingsEvent.ShareApp -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=${context.packageName}")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
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
        Spacer(modifier = Modifier.height(25.dp))

        SettingsTopBar(onBackClick = viewModel::onBackClick)
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (!uiState.isPremium) {
                PremiumSettingsCard(onGetProClick = viewModel::onPremiumClick)
                Spacer(modifier = Modifier.height(30.dp))
            }

            SettingsItem(
                icon = R.drawable.ic_language_selection, // Placeholder for language globe icon if not found
                title = stringResource(R.string.language_selection),
                subtitle = uiState.selectedLanguageName,
                onClick = viewModel::onLanguageClick
            )

            SettingsItem(
                icon = R.drawable.ic_privacy_policy, // Placeholder for privacy icon
                title = stringResource(R.string.privacy_policy),
                subtitle = stringResource(R.string.terms_and_conditions),
                onClick = viewModel::onPrivacyClick
            )

            SettingsItem(
                icon = R.drawable.ic_rate_us, // Placeholder for rate icon
                title = stringResource(R.string.rate_us),
                subtitle = stringResource(R.string.help_us_improve),
                onClick = viewModel::onRateClick
            )

            SettingsItem(
                icon = R.drawable.ic_share_us, // Placeholder for share icon,
                title = stringResource(R.string.tell_a_friend),
                subtitle = stringResource(R.string.share_the_app),
                onClick = viewModel::onShareClick
            )

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
private fun SettingsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
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
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.settings),
            fontFamily = SfProDisplayBold,
            fontSize = 20.sp,
            color = TextColor
        )
    }
}

@Composable
private fun PremiumSettingsCard(onGetProClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd){
            Column(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                        Text(
                            text = stringResource(R.string.unlock_premium),
                            color = PrimaryColor,
                            fontFamily = SfProDisplayBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.upgrade_for_smooth),
                            color = TextColor,
                            fontSize = 12.sp,
                            lineHeight = 13.sp
                        )
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                PremiumFeatureRow(text = stringResource(R.string.unlimited_ai_suit_changer))
                PremiumFeatureRow(text = stringResource(R.string.unlimited_bg_removes))
                PremiumFeatureRow(text = stringResource(R.string.remove_all_ads))

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onGetProClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text(
                        text = stringResource(R.string.get_pro),
                        fontFamily = SfProDisplayBold,
                        fontSize = 18.sp,
                        color = WhiteColor
                    )
                }
            }
            Image(
                painter = painterResource(R.drawable.ic_premium_crown),
                contentDescription = null,
                modifier = Modifier.size(140.dp).rotate(330f).offset(x = 40.dp)
            )
        }

    }
}

@Composable
private fun PremiumFeatureRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextColor,
            fontFamily = SfProDisplayMedium
        )
    }
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(vertical = 12.dp)
            .clickableNoRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(WhiteColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                fontFamily = SfProDisplayBold,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = TextColor
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontFamily = SfProDisplayRegular,
                color = TextColor
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = TextColor,
            modifier = Modifier.height(8.dp).width(5.dp)
        )
    }
}
