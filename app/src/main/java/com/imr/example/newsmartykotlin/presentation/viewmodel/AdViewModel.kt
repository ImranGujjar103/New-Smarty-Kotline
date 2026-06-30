package com.imr.example.newsmartykotlin.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.imr.example.newsmartykotlin.BuildConfig
import com.imr.example.newsmartykotlin.core.ads.AdLoadingController
import com.imr.example.newsmartykotlin.core.ads.AdManager
import com.imr.example.newsmartykotlin.core.ads.AdsConsentManager
import com.imr.example.newsmartykotlin.core.extensions.getCurrentTime
import com.imr.example.newsmartykotlin.core.network.NetworkMonitor
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.data.model.SplashAdConfig
import com.imr.example.newsmartykotlin.domain.repository.AdRepository
import com.imr.example.newsmartykotlin.presentation.language.LanguageNativeState
import com.imr.example.newsmartykotlin.presentation.states.AdState
import com.imr.example.newsmartykotlin.presentation.states.ConsentState
import com.imr.example.newsmartykotlin.presentation.states.NavigationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdViewModel(
    val adRepository: AdRepository,
    val dataStorePrefs: DataStorePrefs,
    val context: Context,
    val networkMonitor: NetworkMonitor,
    private val adsConsentManager: AdsConsentManager
) : ViewModel()
{
    val isConnected: Flow<Boolean> = networkMonitor.isConnected

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Idle)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val _consentState = MutableStateFlow<ConsentState>(ConsentState.Idle)
    val consentState: StateFlow<ConsentState> = _consentState.asStateFlow()

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    private val _nativeAdStates = mutableMapOf<String, MutableStateFlow<LanguageNativeState>>()

    fun getNativeAdState(tag: String): StateFlow<LanguageNativeState> {
        return _nativeAdStates.getOrPut(tag) {
            MutableStateFlow(LanguageNativeState.Idle)
        }.asStateFlow()
    }

    var interstitialAd: InterstitialAd? = null
        private set

    var appOpenAd: AppOpenAd? = null
        private set

    var proAppOpenAd: AppOpenAd? = null
        private set

    var proInterstitialAd: InterstitialAd? = null
        private set

    private var isBannerPreloadCompleted = false
    private var bannerAdView: AdView? = null
    private var bannerShownTime = 0L
    private var hasBannerDelayApplied = false

    private var isPaused = false
    private var isAdLoadCompleted = false
    private var isNativePreloadCompleted = false
    private var shouldLoadAds = false
    private var adLoadStartTime = 0L
    private var isRemoteConfigFetched = false
    private var isFirstSplash = true
    private var loadedAdType: AdType = AdType.NONE

    // Pro splash ad tracking
    private var isProAdLoadCompleted = false
    private var shouldTryProAd = false

    companion object {
        private const val TAG = "AdViewModel"
        private const val SPLASH_DELAY_MS = 3500L
        private var AD_TIMEOUT_MS = 8000L
    }

    enum class AdType {
        NONE,
        INTERSTITIAL,
        APP_OPEN,
        APP_OPEN_PRO,
        INTERSTITIAL_PRO
    }

    fun initialize(activity: Activity) {
        viewModelScope.launch {
            val isPurchased = dataStorePrefs.getIsPurchased().first()
            if (isPurchased) {
                Log.d(TAG, "initialize: User is premium, navigating after 2 seconds")
                delay(2000)
                checkNavigation("Premium user delay")
                return@launch
            }

            isFirstSplash = dataStorePrefs.isFirstSplash().first()
            Log.d(TAG, "initialize: isFirstSplash = $isFirstSplash ==== ${getCurrentTime()}")

            initializeRemoteConfig(activity)
        }
    }

    private suspend fun waitForAdOrTimeout() {
        AD_TIMEOUT_MS = adRepository.appConfig.value.splashDuration.duration * 1000L
        Log.d(TAG, "waitForAdOrTimeout: AD_TIMEOUT_MS = $AD_TIMEOUT_MS, splashDuration = ${adRepository.appConfig.value.splashDuration}")

        if (!shouldLoadAds && !shouldTryProAd) {
            Log.d(TAG, "waitForAdOrTimeout: No ads to load, proceeding")
            return
        }

        val waitingForProAd = shouldTryProAd && !isProAdLoadCompleted
        val waitingForRegularAds = shouldLoadAds && (!isAdLoadCompleted || !isNativePreloadCompleted || !isBannerPreloadCompleted)

        if (!waitingForProAd && !waitingForRegularAds) {
            Log.d(TAG, "waitForAdOrTimeout: All required ads already loaded")
            return
        }

        val elapsedTime = System.currentTimeMillis() - adLoadStartTime
        if (elapsedTime >= AD_TIMEOUT_MS) {
            Log.d(TAG, "waitForAdOrTimeout: Timeout already passed (${elapsedTime}ms), proceeding")
            return
        }

        val remainingTime = AD_TIMEOUT_MS - elapsedTime
        Log.d(TAG, "waitForAdOrTimeout: Waiting for ads... ProAd: $waitingForProAd, RegularAds: $waitingForRegularAds, Remaining timeout: ${remainingTime}ms")

        val startWaitTime = System.currentTimeMillis()

        // If trying pro ad, wait for it first with shorter timeout (e.g., 3 seconds)
        if (waitingForProAd) {
            val proAdTimeout = kotlin.comparisons.minOf(3000L, remainingTime)
            while (!isProAdLoadCompleted && (System.currentTimeMillis() - startWaitTime) < proAdTimeout) {
                delay(100)
            }

            val proWaitTime = System.currentTimeMillis() - startWaitTime
            if (isProAdLoadCompleted && (proAppOpenAd != null || proInterstitialAd != null)) {
                Log.d(TAG, "waitForAdOrTimeout: Pro ad loaded successfully after ${proWaitTime}ms")
                applyBannerDelayIfNeeded()
                return
            } else {
                Log.d(TAG, "waitForAdOrTimeout: Pro ad failed or timed out after ${proWaitTime}ms, falling back to regular ads")
            }
        }

        // Wait for regular ads
        val currentWaitTime = System.currentTimeMillis() - startWaitTime
        val remainingRegularTime = remainingTime - currentWaitTime

        while ((!isAdLoadCompleted || !isNativePreloadCompleted || !isBannerPreloadCompleted)
            && (System.currentTimeMillis() - startWaitTime) < remainingTime) {
            delay(100)
        }

        val waitedTime = System.currentTimeMillis() - startWaitTime
        if (isAdLoadCompleted && isNativePreloadCompleted && isBannerPreloadCompleted) {
            Log.d(TAG, "waitForAdOrTimeout: All ads loaded after waiting ${waitedTime}ms")
            applyBannerDelayIfNeeded()
        } else {
            Log.d(TAG, "waitForAdOrTimeout: Timeout after ${waitedTime}ms (splash=$isAdLoadCompleted, native=$isNativePreloadCompleted, banner=$isBannerPreloadCompleted)")
        }
    }

    private suspend fun applyBannerDelayIfNeeded() {
        val bannerConfig = adRepository.appConfig.value.splashBanner
        if (bannerConfig.toShow && dataStorePrefs.getIsPurchased().first().not()) {
            if (bannerShownTime > 0 && !hasBannerDelayApplied) {
                val elapsedSinceBanner = System.currentTimeMillis() - bannerShownTime
                val remainingDelay = 3000L - elapsedSinceBanner
                if (remainingDelay > 0) {
                    Log.d(TAG, "applyBannerDelayIfNeeded: Waiting extra ${remainingDelay}ms after banner impression")
                    delay(remainingDelay)
                }
                AD_TIMEOUT_MS += 3000L
                hasBannerDelayApplied = true
                Log.d(TAG, "applyBannerDelayIfNeeded: Extended AD_TIMEOUT_MS by 3000ms (new total = $AD_TIMEOUT_MS)")
            }
        } else {
            Log.d(TAG, "applyBannerDelayIfNeeded: Banner disabled or user is premium — skipping extra delay")
        }
    }

    fun getPreloadedSplashBanner(): AdView? = bannerAdView

    fun onBannerShown() {
        bannerShownTime = System.currentTimeMillis()
        hasBannerDelayApplied = false
        Log.d(TAG, "onBannerShown: Banner impression recorded at $bannerShownTime")
    }

    fun onPause() {
        isPaused = true
        Log.d(TAG, "onPause: isPaused = true")
    }

    fun onResume() {
        val wasPaused = isPaused
        isPaused = false
        Log.d(TAG, "onResume: isPaused = false, wasPaused = $wasPaused, current navigation state = ${_navigationState.value}")

        if (wasPaused && _navigationState.value == NavigationState.Idle && (isProAdLoadCompleted || isAdLoadCompleted)) {
            Log.d(TAG, "onResume: Retrying navigation after unpause")
            checkNavigation("Resume----------")
        }
    }

    private fun requestConsent(activity: Activity) {
        if (adsConsentManager.canRequestAds) {
            onConsentCompleted(activity)
        } else {
            adsConsentManager.showGDPRConsent(activity, BuildConfig.DEBUG) {
                onConsentCompleted(activity)
            }
        }
    }

    fun onConsentCompleted(activity: Activity) {
        viewModelScope.launch {
            dataStorePrefs.setIsConsent(true)
            _consentState.value = ConsentState.Completed

            val isPurchased = dataStorePrefs.getIsPurchased().first()
            val isConnected = networkMonitor.isConnected.first()
            shouldLoadAds = !isPurchased && isConnected && adRepository.appConfig.value.adShow

            if (shouldLoadAds) {
                adLoadStartTime = System.currentTimeMillis()
                Log.d(TAG, "onConsentCompleted: Set adLoadStartTime = $adLoadStartTime ==== ${getCurrentTime()}")
                startAdLoading(activity)
                waitForAdOrTimeout()
            }
            checkNavigation("Consent completed -------")
        }
    }

    private fun initializeRemoteConfig(activity: Activity) {
        Log.d(TAG, "initializeRemoteConfig: Started ==== ${getCurrentTime()}")
        viewModelScope.launch {
            _adState.value = AdState.FetchingConfig

            val result = adRepository.fetchRemoteConfig()
            result.onSuccess { config ->
                Log.d(TAG, "Remote config loaded successfully ==== ${getCurrentTime()}")
                isRemoteConfigFetched = true
                checkInitialFlow(activity)
            }.onFailure { error ->
                Log.e(TAG, "Failed to fetch remote config: ${error.message}")
                isRemoteConfigFetched = true
                checkInitialFlow(activity)
            }
        }
    }

    private fun checkInitialFlow(activity: Activity) {
        Log.d(TAG, "==== checkInitialFlow ==== ${getCurrentTime()}")
        viewModelScope.launch {
            val isPurchased = dataStorePrefs.getIsPurchased().first()
            val isConnected = networkMonitor.isConnected.first()
            val hasConsent = dataStorePrefs.getIsConsent().first()

            shouldLoadAds = !isPurchased && isConnected && adRepository.appConfig.value.adShow

            when {
                !hasConsent && isConnected -> {
                    Log.d(TAG, "checkInitialFlow: Requires consent ==== ${getCurrentTime()}")
                    _consentState.value = ConsentState.RequiresConsent
                    requestConsent(activity)
                }
                shouldLoadAds -> {
                    Log.d(TAG, "checkInitialFlow: Loading ads (not premium, has internet) ==== ${getCurrentTime()}")

                    adLoadStartTime = System.currentTimeMillis()
                    Log.d(TAG, "checkInitialFlow: Set adLoadStartTime = $adLoadStartTime ==== ${getCurrentTime()}")

                    startAdLoading(activity)
                    waitForAdOrTimeout()
                    checkNavigation("Loading ads -------")
                }
                else -> {
                    Log.d(TAG, "checkInitialFlow: Skipping ads (premium: $isPurchased, internet: $isConnected) ==== ${getCurrentTime()}")
                    checkNavigation("Skipping ads -------")
                }
            }
        }
    }

    private fun startAdLoading(activity : Activity) {
        Log.d(TAG, "startAdLoading: shouldLoadAds = $shouldLoadAds, isFirstSplash = $isFirstSplash ==== ${getCurrentTime()}")

        if (!shouldLoadAds) {
            Log.d(TAG, "startAdLoading: Skipping ad load - user is premium, no internet or adShow is false ==== ${getCurrentTime()}")
            isAdLoadCompleted = true
            isNativePreloadCompleted = true
            isBannerPreloadCompleted = true
            isProAdLoadCompleted = true
            return
        }

        Log.d(TAG, "==== MobileAds.initialize (Next Gen) ==== ${getCurrentTime()}")
        viewModelScope.launch {
                val splashAdConfig = adRepository.appConfig.value.splashAd
                val splashProConfig = adRepository.appConfig.value.splashProAd
                val bannerConfig = adRepository.appConfig.value.splashBanner
                bannerConfig.toShow = false

                // ✅ Banner preload
                if (bannerConfig.toShow && bannerConfig.adId.isNotEmpty()) {
                    preloadSplashBannerAd(activity, bannerConfig.adId)
                } else {
                    isBannerPreloadCompleted = true
                }
                isNativePreloadCompleted = true
                // Native preload
//                if (isFirstSplash) {
//                    preloadNativeAd()
//                } else {
//                    isNativePreloadCompleted = true
//                }

                // 🟢 NEW: Try Pro Splash Ad first if enabled
                if (splashProConfig.toShow) {
                    Log.d(TAG, "startAdLoading: Pro splash ad enabled, checking ad type: ${splashProConfig.adType}")
                    shouldTryProAd = true

                    when (splashProConfig.adType) {
                        0 -> {
                            // Interstitial Pro
                            if (splashProConfig.interIdPro.isNotEmpty()) {
                                Log.d(TAG, "startAdLoading: Attempting to load Pro Interstitial Ad")
                                loadSplashInterstitialProAd(splashProConfig.interIdPro) { proAd ->
                                    if (proAd != null) {
                                        Log.d(TAG, "startAdLoading: Pro Interstitial Ad loaded successfully, skipping regular splash ads")
                                        isAdLoadCompleted = true
                                    } else {
                                        Log.d(TAG, "startAdLoading: Pro Interstitial Ad failed, loading regular splash ads as fallback")
                                        viewModelScope.launch {
                                            loadRegularSplashAds(splashAdConfig)
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "startAdLoading: Pro Interstitial ID empty, loading regular splash ads")
                                shouldTryProAd = false
                                isProAdLoadCompleted = true
                                loadRegularSplashAds(splashAdConfig)
                            }
                        }
                        1 -> {
                            // App Open Pro
                            if (splashProConfig.appOpenIdPro.isNotEmpty()) {
                                Log.d(TAG, "startAdLoading: Attempting to load Pro App Open Ad")
                                loadSplashAppOpenProAd(splashProConfig.appOpenIdPro) { proAd ->
                                    if (proAd != null) {
                                        Log.d(TAG, "startAdLoading: Pro App Open Ad loaded successfully, skipping regular splash ads")
                                        isAdLoadCompleted = true
                                    } else {
                                        Log.d(TAG, "startAdLoading: Pro App Open Ad failed, loading regular splash ads as fallback")
                                        viewModelScope.launch {
                                            loadRegularSplashAds(splashAdConfig)
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "startAdLoading: Pro App Open ID empty, loading regular splash ads")
                                shouldTryProAd = false
                                isProAdLoadCompleted = true
                                loadRegularSplashAds(splashAdConfig)
                            }
                        }
                        else -> {
                            // No pro ad
                            Log.d(TAG, "startAdLoading: Pro ad type is ${splashProConfig.adType}, loading regular splash ads")
                            shouldTryProAd = false
                            isProAdLoadCompleted = true
                            loadRegularSplashAds(splashAdConfig)
                        }
                    }
                } else {
                    // Pro ad not enabled, load regular splash ads
                    Log.d(TAG, "startAdLoading: Pro ad not enabled, loading regular splash ads")
                    shouldTryProAd = false
                    isProAdLoadCompleted = true
                    loadRegularSplashAds(splashAdConfig)
                }
            }
        }

    private suspend fun loadSplashAppOpenProAd(adId: String, callback: (AppOpenAd?) -> Unit) {
        Log.d(TAG, "loadSplashAppOpenProAd: adId = $adId ==== ${getCurrentTime()}")

        _adState.value = AdState.Loading

        withContext(Dispatchers.Main) {
            AdManager.loadAppOpenAd(
                adId = adId,
                tag = "Splash_AppOpen_Pro"
            ) { ad ->
                proAppOpenAd = ad

                if (ad != null) {
                    loadedAdType = AdType.APP_OPEN_PRO
                    Log.d(TAG, "==== Pro App Open Ad loaded successfully ==== ${getCurrentTime()}")
                    _adState.value = AdState.Ready
                } else {
                    loadedAdType = AdType.NONE
                    Log.e(TAG, "==== Pro App Open Ad failed to load ==== ${getCurrentTime()}")
                    _adState.value = AdState.Failed("Pro app open ad load failed")
                }

                isProAdLoadCompleted = true
                callback.invoke(ad)
            }
        }
    }

    private suspend fun loadSplashInterstitialProAd(adId: String, callback: (InterstitialAd?) -> Unit) {
        Log.d(TAG, "loadSplashInterstitialProAd: adId = $adId ==== ${getCurrentTime()}")

        _adState.value = AdState.Loading

        withContext(Dispatchers.Main) {
            AdManager.loadInterstitialAd(
                adId = adId,
                tag = "Splash_Inter_Pro"
            ) { ad ->
                proInterstitialAd = ad

                if (ad != null) {
                    loadedAdType = AdType.INTERSTITIAL_PRO
                    Log.d(
                        TAG,
                        "==== Pro Interstitial Ad loaded successfully ==== ${getCurrentTime()}"
                    )
                    _adState.value = AdState.Ready
                } else {
                    loadedAdType = AdType.NONE
                    Log.e(TAG, "==== Pro Interstitial Ad failed to load ==== ${getCurrentTime()}")
                    _adState.value = AdState.Failed("Pro interstitial ad load failed")
                }

                isProAdLoadCompleted = true
                callback.invoke(ad)
            }
        }
    }

    private suspend fun loadRegularSplashAds(splashAd: SplashAdConfig) {
        when {
            splashAd.toShow && splashAd.adType == 1 && splashAd.appOpenId.isNotEmpty() -> {
                Log.d(TAG, "loadRegularSplashAds: Loading App Open Ad")
                loadSplashAppOpenAd(splashAd.appOpenId) {
                    if (it == null) {
                        _navigationState.value = NavigationState.ADFailed
                    }
                }
            }
            splashAd.toShow && splashAd.adType == 0 && splashAd.interId.isNotEmpty() -> {
                Log.d(TAG, "loadRegularSplashAds: Loading Interstitial Ad")
                loadSplashInterstitialAd(splashAd.interId) {
                    if (it == null) {
                        _navigationState.value = NavigationState.ADFailed
                    }
                }
            }
            else -> {
                Log.d(TAG, "loadRegularSplashAds: No splash ads enabled")
                isAdLoadCompleted = true
            }
        }
    }

    fun showInterstitialAd(
        activity: Activity,
        toShow: Boolean,
        adLoadingController: AdLoadingController,
        callback: () -> Unit
    ) {
        if (toShow) {
            AdManager.showInterstitialAd(
                activity = activity,
                adLoadingController = adLoadingController,
                scope = viewModelScope,
                callback = callback
            )
        } else {
            callback.invoke()
        }
    }

    fun showInterstitialAdWithCallBack(
        activity: Activity,
        toShow: Boolean,
        tag: String,
        adId: String,
        adLoadingController: AdLoadingController,
        callback: () -> Unit
    ) {
        if (toShow) {
            AdManager.showInterstitialAdWithCallBack(
                activity = activity,
                tag = tag,
                adId = adId,
                adLoadingController = adLoadingController,
                scope = viewModelScope,
                callback = callback
            )
        } else {
            callback.invoke()
        }
    }

    fun showAppOpenAdWithCallBack(
        activity: Activity,
        toShow: Boolean,
        tag: String,
        adId: String,
        adLoadingController: AdLoadingController,
        callback: () -> Unit
    ) {
        if (toShow) {
            AdManager.showAppOpenAdWithCallBack(
                activity = activity,
                tag = tag,
                adId = adId,
                adLoadingController = adLoadingController,
                scope = viewModelScope,
                callback = callback
            )
        } else {
            callback.invoke()
        }
    }
    private suspend fun preloadSplashBannerAd(activity: Activity, adId: String) {
        Log.d(TAG, "preloadSplashBannerAd: adId = $adId ==== ${getCurrentTime()}")

        withContext(Dispatchers.Main) {
            AdManager.loadAdaptiveBanner(
                activity = activity,
                adId = adId,
                tag = "SplashBanner"
            ) { adView ->
                bannerAdView = adView
                _adState.value = AdState.BannerLoaded
                isBannerPreloadCompleted = true
                Log.d(
                    TAG,
                    "preloadSplashBannerAd: Banner preloaded = ${adView != null} ==== ${getCurrentTime()}"
                )
            }
        }
    }

    private suspend fun preloadNativeAd() {
        Log.d(TAG, "preloadNativeAd: ==== ${getCurrentTime()}")
        val nativeConfig = adRepository.appConfig.value.languageNative

        if (!nativeConfig.toShow) {
            Log.d(TAG, "preloadNativeAd: Native ad disabled, skipping")
            isNativePreloadCompleted = true
            return
        }

        withContext(Dispatchers.Main) {
            AdManager.loadNativeAd(
                adId = nativeConfig.adId,
                tag = "languageNative"
            ) { ad ->
                Log.d(TAG, "Native ad preloaded: ${ad != null} ==== ${getCurrentTime()}")
                isNativePreloadCompleted = true
            }
        }
    }

    fun loadNativeAd(adId: String, tag: String, callback: (NativeAd?) -> Unit) {
        val stateFlow = _nativeAdStates.getOrPut(tag) {
            MutableStateFlow(LanguageNativeState.Idle)
        }

        if (stateFlow.value is LanguageNativeState.Loaded) {
            Log.d(TAG, "loadNativeAd: tag = $tag - returning cached ad")
            callback.invoke((stateFlow.value as LanguageNativeState.Loaded).nativeAd)
            return
        }

        if (stateFlow.value is LanguageNativeState.Loading) {
            Log.d(TAG, "loadNativeAd: tag = $tag - already loading")
        }

        stateFlow.value = LanguageNativeState.Loading
        Log.d(TAG, "loadNativeAd: tag = $tag")
        AdManager.loadNativeAd(adId = adId, tag = tag) { ad ->
            stateFlow.value = if (ad != null) {
                LanguageNativeState.Loaded(ad)
            } else {
                LanguageNativeState.Failed
            }
            callback.invoke(ad)
        }
    }

    fun loadAdaptiveBanner(activity: Activity, adId: String, tag: String, callback: (AdView?) -> Unit) {
        Log.d(TAG, "loadAdaptiveBanner: tag = $tag")
        AdManager.loadAdaptiveBanner(activity, adId = adId, tag = tag) {
            callback.invoke(it)
        }
    }

    suspend fun loadSplashAppOpenAd(adId: String?, callback: (AppOpenAd?) -> Unit) {
        Log.d(TAG, "loadSplashAppOpenAd: adId = $adId ==== ${getCurrentTime()}")
        if (adId == null) {
            isAdLoadCompleted = true
            return
        }

        _adState.value = AdState.Loading

        withContext(Dispatchers.Main) {
            AdManager.loadAppOpenAd(adId = adId, tag = "Splash_AppOpen") { ad ->
                callback.invoke(ad)
                appOpenAd = ad
                interstitialAd = null

                if (ad != null) {
                    loadedAdType = AdType.APP_OPEN
                    Log.d(TAG, "==== App Open Ad loaded successfully ==== ${getCurrentTime()}")
                } else {
                    loadedAdType = AdType.NONE
                    Log.e(TAG, "==== App Open Ad failed to load ==== ${getCurrentTime()}")
                }

                isAdLoadCompleted = true
                _adState.value = if (ad != null) AdState.Ready else AdState.Failed("Load failed")
                Log.d(TAG, "loadSplashAppOpenAd: Ad loading completed, isAdLoadCompleted = true")
            }
        }
    }

    suspend fun loadSplashInterstitialAd(adId: String?, callback: (InterstitialAd?) -> Unit) {
        Log.d(TAG, "loadSplashInterstitialAd: adId = $adId ==== ${getCurrentTime()}")
        if (adId == null) {
            isAdLoadCompleted = true
            return
        }

        _adState.value = AdState.Loading

        withContext(Dispatchers.Main) {
            AdManager.loadInterstitialAd(adId = adId, tag = "Splash_Inter") { ad ->
                callback.invoke(ad)
                interstitialAd = ad
                appOpenAd = null

                if (ad != null) {
                    loadedAdType = AdType.INTERSTITIAL
                    Log.d(TAG, "==== Interstitial Ad loaded successfully ==== ${getCurrentTime()}")
                } else {
                    loadedAdType = AdType.NONE
                    Log.e(TAG, "==== Interstitial Ad failed to load ==== ${getCurrentTime()}")
                }

                isAdLoadCompleted = true
                _adState.value = if (ad != null) AdState.Ready else AdState.Failed("Load failed")
                Log.d(
                    TAG,
                    "loadSplashInterstitialAd: Ad loading completed, isAdLoadCompleted = true"
                )
            }
        }
    }

    fun loadAppOpen(adId: String, tag: String, callback: (AppOpenAd?) -> Unit) {
        AdManager.loadAppOpenAd(adId, tag, callback)
    }

     fun loadInterstitialAd(adId: String, tag: String) {
        Log.d(TAG, "loadInterstitialAd: adId = $adId ==== ${getCurrentTime()}")
        AdManager.loadInterstitialAd(adId = adId, tag = tag) { ad ->
            Log.d(TAG, "==== interstitialAd loaded ==== ${getCurrentTime()}")
        }
    }

    fun loadInterstitialAd(adId: String, tag: String, callback: (InterstitialAd?) -> Unit) {
        Log.d(TAG, "loadInterstitialAd: adId = $adId ==== ${getCurrentTime()}")
        AdManager.loadInterstitialAd(adId = adId, tag = tag) { ad ->
            interstitialAd = ad
            callback.invoke(interstitialAd)
            Log.d(TAG, "==== interstitialAd loaded ==== ${getCurrentTime()}")
        }
    }

    private fun checkNavigation(navFrom: String) {
        Log.d(TAG, "checkNavigation: from ====== $navFrom")

        viewModelScope.launch {
            val isPurchased = dataStorePrefs.getIsPurchased().first()
            val config = adRepository.appConfig.value

            val adTypeToShow = when {
                proInterstitialAd != null -> AdType.INTERSTITIAL_PRO
                proAppOpenAd != null -> AdType.APP_OPEN_PRO
                appOpenAd != null -> AdType.APP_OPEN
                interstitialAd != null -> AdType.INTERSTITIAL
                else -> AdType.NONE
            }

            val shouldShowAd = (shouldLoadAds || shouldTryProAd) && adTypeToShow != AdType.NONE

            Log.d(TAG, "checkNavigation: isPaused = $isPaused")
            Log.d(TAG, "checkNavigation: isFirstSplash = $isFirstSplash")
            Log.d(TAG, "checkNavigation: isPurchased = $isPurchased")
            Log.d(TAG, "checkNavigation: premiumAfterSplashShow = ${config.premiumAfterSplashShow}")
            Log.d(TAG, "checkNavigation: premiumHomeShow = ${config.premiumHomeShow}")
            Log.d(TAG, "checkNavigation: shouldShowAd = $shouldShowAd")
            Log.d(TAG, "checkNavigation: adType = $adTypeToShow")

            _navigationState.value = when {

                /*
                 * FIRST TIME USER
                 *
                 * Splash -> Language -> Onboarding -> Premium -> Home
                 *
                 * Important:
                 * Premium should NOT be shown directly after Splash on first time.
                 * It will be shown after Onboarding.
                 */
                isFirstSplash -> {


                    NavigationState.NavigateToLanguage(
                        showAd = shouldShowAd,
                        adType = adTypeToShow
                    )
                }

                /*
                 * SECOND TIME USER
                 *
                 * Splash -> Premium -> Home
                 *
                 * Controlled by Remote Config.
                 */
                !isPurchased && config.premiumHomeShow -> {
                    NavigationState.NavigateToPremium(
                        showAd = shouldShowAd,
                        adType = adTypeToShow
                    )
                }

                /*
                 * SECOND TIME USER
                 *
                 * Splash -> Home
                 */
                else -> {
                    NavigationState.NavigateToMain(
                        showAd = shouldShowAd,
                        adType = adTypeToShow
                    )
                }
            }

            Log.d(TAG, "checkNavigation: Navigation state set to: ${_navigationState.value}")
        }
    }
    fun resetNavigationState() {
        _navigationState.value = NavigationState.Idle
        Log.d(TAG, "resetNavigationState: Reset to Idle")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: Clearing ViewModel")
        interstitialAd = null
        appOpenAd = null
        proAppOpenAd = null
        proInterstitialAd = null
    }
}
