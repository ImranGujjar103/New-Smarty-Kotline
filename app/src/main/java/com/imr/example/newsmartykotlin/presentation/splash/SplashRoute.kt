package com.imr.example.newsmartykotlin.presentation.splash

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imr.example.newsmartykotlin.BuildConfig
import com.imr.example.newsmartykotlin.core.ads.AdLoadingController
import com.imr.example.newsmartykotlin.core.ads.AdsConsentManager
import com.imr.example.newsmartykotlin.core.extensions.enableFullScreenMode
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.presentation.states.AdState
import com.imr.example.newsmartykotlin.presentation.states.ConsentState
import com.imr.example.newsmartykotlin.presentation.states.NavigationState
import com.imr.example.newsmartykotlin.presentation.viewmodel.AdViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SplashRoute(
    onNavigateToLanguage: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AdViewModel = koinViewModel(),
    dataStorePrefs: DataStorePrefs = koinInject(),
    adsConsentManager: AdsConsentManager = koinInject(),
    adLoadingController: AdLoadingController = koinInject()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    val adState by viewModel.adState.collectAsStateWithLifecycle()
    val navigationState by viewModel.navigationState.collectAsStateWithLifecycle()
    val consentState by viewModel.consentState.collectAsStateWithLifecycle()

    var isAdFailed by remember { mutableStateOf(false) }
    var isPremiumUser by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        activity.enableFullScreenMode()
        scope.launch {
            dataStorePrefs.getIsPurchased().collect {
                isPremiumUser = it
            }
        }

        delay(1000)
        viewModel.initialize(activity)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onResume()
                Lifecycle.Event.ON_PAUSE -> viewModel.onPause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = true) {}

    LaunchedEffect(consentState) {
        if (consentState is ConsentState.RequiresConsent) {
            val isPurchased = dataStorePrefs.getIsPurchased().first()

            if (isPurchased) {
                viewModel.onConsentCompleted(activity)
                return@LaunchedEffect
            }

            try {
                if (!adsConsentManager.canRequestAds) {
                    adsConsentManager.showGDPRConsent(
                        activity = activity,
                        isTest = BuildConfig.DEBUG
                    ) { error ->
                        error?.let {
                            Log.e("SplashRoute", "Consent error: ${it.errorCode} - ${it.message}")
                        }
                        viewModel.onConsentCompleted(activity)
                    }
                } else {
                    viewModel.onConsentCompleted(activity)
                }
            } catch (e: Exception) {
                Log.e("SplashRoute", "Consent exception: ${e.message}", e)
                viewModel.onConsentCompleted(activity)
            }
        }
    }

    LaunchedEffect(adState) {
        when (adState) {
            is AdState.Failed -> {
                if (viewModel.adRepository.appConfig.value.premiumAdFailedShow) {
                    isAdFailed = true
                }
            }

            else -> Unit
        }
    }

    LaunchedEffect(navigationState) {
        when (val state = navigationState) {

            is NavigationState.NavigateToLanguage -> {
                handleSplashNavigation(
                    activity = activity,
                    viewModel = viewModel,
                    adLoadingController = adLoadingController,
                    showAd = state.showAd,
                    adType = state.adType,
                    isAdFailed = isAdFailed,
                    moveToPremium = onNavigateToPremium,
                    moveNext = onNavigateToLanguage
                )
            }

            is NavigationState.NavigateToMain -> {
                handleSplashNavigation(
                    activity = activity,
                    viewModel = viewModel,
                    adLoadingController = adLoadingController,
                    showAd = state.showAd,
                    adType = state.adType,
                    isAdFailed = isAdFailed,
                    moveToPremium = onNavigateToPremium,
                    moveNext = {
                        val config = viewModel.adRepository.appConfig.value

                        if (config.premiumHomeShow && !isPremiumUser) {
                            onNavigateToPremium()
                        } else {
                            onNavigateToHome()
                        }
                    }
                )
            }

            is NavigationState.NavigateToPremium -> {
                onNavigateToPremium()
                viewModel.resetNavigationState()
            }

            else -> Unit
        }
    }

    SplashScreen(
        showBanner = adState is AdState.Loading || adState is AdState.BannerLoaded,
        bannerAdView = viewModel.getPreloadedSplashBanner(),
        onBannerShown = viewModel::onBannerShown
    )
}

private fun handleSplashNavigation(
    activity: Activity,
    viewModel: AdViewModel,
    adLoadingController: AdLoadingController,
    showAd: Boolean,
    adType: AdViewModel.AdType,
    isAdFailed: Boolean,
    moveToPremium: () -> Unit,
    moveNext: () -> Unit
) {
    fun finishNavigation() {
        if (isAdFailed) {
            moveToPremium()
        } else {
            moveNext()
        }
        viewModel.resetNavigationState()
    }

    if (!showAd) {
        finishNavigation()
        return
    }

    val config = viewModel.adRepository.appConfig.value

    when (adType) {
        AdViewModel.AdType.INTERSTITIAL_PRO -> {
            viewModel.showInterstitialAdWithCallBack(
                activity = activity,
                toShow = config.splashProAd.toShow,
                tag = "Splash_Inter_Pro",
                adId = config.splashProAd.interIdPro,
                adLoadingController = adLoadingController
            ) {
                finishNavigation()
            }
        }

        AdViewModel.AdType.APP_OPEN_PRO -> {
            viewModel.showAppOpenAdWithCallBack(
                activity = activity,
                toShow = config.splashProAd.toShow,
                tag = "Splash_appOpenPro",
                adId = config.splashProAd.appOpenIdPro,
                adLoadingController = adLoadingController
            ) {
                finishNavigation()
            }
        }

        AdViewModel.AdType.INTERSTITIAL -> {
            viewModel.showInterstitialAdWithCallBack(
                activity = activity,
                toShow = config.splashAd.toShow,
                tag = "Splash_Inter",
                adId = config.splashAd.interId,
                adLoadingController = adLoadingController
            ) {
                finishNavigation()
            }
        }

        AdViewModel.AdType.APP_OPEN -> {
            viewModel.showAppOpenAdWithCallBack(
                activity = activity,
                toShow = config.splashAd.toShow,
                tag = "Splash_appOpen",
                adId = config.splashAd.appOpenId,
                adLoadingController = adLoadingController
            ) {
                finishNavigation()
            }
        }

        else -> finishNavigation()
    }
}