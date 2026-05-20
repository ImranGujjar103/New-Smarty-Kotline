package com.imr.example.newsmartykotlin.core.ads

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log

import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.imr.example.newsmartykotlin.MyApp
import com.imr.example.newsmartykotlin.core.extensions.showLogsAppOpen
import com.imr.example.newsmartykotlin.core.extensions.showLogsBanner
import com.imr.example.newsmartykotlin.core.extensions.showLogsInter
import com.imr.example.newsmartykotlin.core.extensions.showLogsNative
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.let
import kotlin.run

object AdManager {
    private const val TAG = "AdManager"
    const val AD_SETTING_RELEASE = "ad_monetization_release"
    const val AD_SETTING_DEBUG = "ad_strategy_debug"
    // Single ad cache per type (no tags)
    private var cachedInterstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false
    private val interstitialWaitingCallbacks = mutableListOf<(InterstitialAd?) -> Unit>()

    private var cachedAppOpenAd: AppOpenAd? = null
    private var isLoadingAppOpen = false
    private val appOpenWaitingCallbacks = mutableListOf<(AppOpenAd?) -> Unit>()

    private var cachedNativeAd: NativeAd? = null
    private var isLoadingNative = false
    private val nativeWaitingCallbacks = mutableListOf<(NativeAd?) -> Unit>()

    private var cachedBannerAdView: AdView? = null
    private var isLoadingBanner = false
    private val bannerWaitingCallbacks = mutableListOf<(AdView?) -> Unit>()

    /**
     * Load an Interstitial Ad (single instance)
     */
    fun loadInterstitialAd(
        adId: String,
        tag: String,
        callback: (InterstitialAd?) -> Unit
    ) {
        showLogsInter("$tag interstitial ad loading adId = $adId")
        Log.d(TAG, "loadInterstitialAd: tag = $tag, adId = $adId")

        // Case 1: Already have cached ad
        cachedInterstitialAd?.let {
            showLogsInter("$tag interstitial ad already cached")
            Log.d(TAG, "Interstitial ad already cached")
            callback(it)
            return
        }

        // Case 2: Already loading → enqueue callback
        if (isLoadingInterstitial) {
            showLogsInter("$tag interstitial ad already loading, enqueueing callback")
            Log.d(TAG, "Interstitial ad already loading, enqueueing callback")
            interstitialWaitingCallbacks.add(callback)
            return
        }

        // Case 3: Start a new load
        isLoadingInterstitial = true
        interstitialWaitingCallbacks.add(callback)

        val adRequest = AdRequest.Builder().build()

        MyApp.mInstance?.let {
            InterstitialAd.load(
                it,
                adId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        showLogsInter("$tag interstitial ad loaded adId = $adId")
                        Log.d(TAG, "Interstitial ad loaded: tag = $tag")

                        cachedInterstitialAd = ad
                        isLoadingInterstitial = false

                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                showLogsInter("$tag interstitial ad dismissed adId = $adId")
                                Log.d(TAG, "Interstitial ad dismissed: tag = $tag")
                                cachedInterstitialAd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                showLogsInter("$tag interstitial ad failed to show adId = $adId")
                                Log.e(TAG, "Interstitial ad failed to show: tag = $tag, error = ${adError.message}")
                                cachedInterstitialAd = null
                            }

                            override fun onAdShowedFullScreenContent() {
                                showLogsInter("$tag interstitial ad showed adId = $adId")
                                Log.d(TAG, "Interstitial ad showed: tag = $tag")
                            }

                            override fun onAdImpression() {
                                showLogsInter("$tag interstitial ad impression adId = $adId")
                                Log.d(TAG, "Interstitial ad impression: tag = $tag")
                                cachedInterstitialAd = null
                            }
                        }

                        flushInterstitialCallbacks(ad)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        showLogsInter("$tag interstitial ad failed to load adId = $adId")
                        Log.e(TAG, "Interstitial ad failed to load: tag = $tag, error = ${error.message}")
                        isLoadingInterstitial = false
                        flushInterstitialCallbacks(null)
                    }
                }
            )
        }
    }
    fun showInterstitialAd(
        activity: Activity,
        adLoadingController: AdLoadingController,
        scope: CoroutineScope,
        callback: () -> Unit
    ) {

        cachedInterstitialAd?.let { ad ->

            adLoadingController.show(
                scope = scope
            ) {
                callback.invoke()
                ad.show(activity)
            }

        } ?: run {
            callback.invoke()
        }
    }

    fun showInterstitialAdWithCallBack(
        activity: Activity,
        tag: String,
        adId: String,
        adLoadingController: AdLoadingController,
        scope: CoroutineScope,
        callback: () -> Unit
    ) {

        cachedInterstitialAd?.let { ad ->

            adLoadingController.show(
                scope = scope
            ) {

                ad.fullScreenContentCallback = object : FullScreenContentCallback() {

                    override fun onAdDismissedFullScreenContent() {

                        AdLoadingState.hide()

                        showLogsInter(
                            "$tag interstitial ad dismissed adId = $adId"
                        )

                        callback.invoke()

                        cachedInterstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(
                        adError: AdError
                    ) {

                        AdLoadingState.hide()

                        showLogsInter(
                            "Interstitial ad failed to show"
                        )

                        callback.invoke()

                        cachedInterstitialAd = null
                    }

                    override fun onAdShowedFullScreenContent() {

                        showLogsInter(
                            "$tag interstitial ad showed adId = $adId"
                        )
                    }
                }

                ad.show(activity)
            }

        } ?: run {
            callback.invoke()
        }
    }

    fun showAppOpenAdWithCallBack(
        activity: Activity,
        tag: String,
        adId: String,
        adLoadingController: AdLoadingController,
        scope: CoroutineScope,
        callback: () -> Unit
    ) {

        cachedAppOpenAd?.let { ad ->

            adLoadingController.show(
                scope = scope
            ) {

                ad.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {

                            AdLoadingState.hide()

                            showLogsAppOpen(
                                "$tag app open ad dismissed adId = $adId"
                            )

                            Log.d(
                                TAG,
                                "App Open ad dismissed: tag = $tag"
                            )

                            callback.invoke()

                            cachedAppOpenAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(
                            adError: AdError
                        ) {

                            AdLoadingState.hide()

                            callback.invoke()

                            showLogsAppOpen(
                                "$tag app open ad failed to show adId = $adId"
                            )

                            Log.e(
                                TAG,
                                "App Open ad failed to show: tag = $tag, error = ${adError.message}"
                            )

                            cachedAppOpenAd = null
                        }

                        override fun onAdShowedFullScreenContent() {

                            showLogsAppOpen(
                                "$tag app open ad showed adId = $adId"
                            )

                            Log.d(
                                TAG,
                                "App Open ad showed: tag = $tag"
                            )
                        }

                        override fun onAdImpression() {

                            showLogsAppOpen(
                                "$tag app open ad impression adId = $adId"
                            )

                            Log.d(
                                TAG,
                                "App Open ad impression: tag = $tag"
                            )

                            cachedAppOpenAd = null
                        }
                    }

                ad.show(activity)
            }

        } ?: run {
            callback.invoke()
        }
    }

    private fun flushInterstitialCallbacks(ad: InterstitialAd?) {
        interstitialWaitingCallbacks.forEach { it(ad) }
        interstitialWaitingCallbacks.clear()
    }

    /**
     * Load an App Open Ad (single instance)
     */
    fun loadAppOpenAd(
        adId: String,
        tag: String,
        callback: (AppOpenAd?) -> Unit
    ) {
        showLogsAppOpen("$tag app open ad loading adId = $adId")
        Log.d(TAG, "loadAppOpenAd: tag = $tag, adId = $adId")

        // Case 1: Already have cached ad
        cachedAppOpenAd?.let {
            showLogsAppOpen("$tag app open ad already cached")
            Log.d(TAG, "App Open ad already cached")
            callback(it)
            return
        }

        // Case 2: Already loading → enqueue callback
        if (isLoadingAppOpen) {
            showLogsAppOpen("$tag app open ad already loading, enqueueing callback")
            Log.d(TAG, "App Open ad already loading, enqueueing callback")
            appOpenWaitingCallbacks.add(callback)
            return
        }

        // Case 3: Start a new load
        isLoadingAppOpen = true
        appOpenWaitingCallbacks.add(callback)

        val adRequest = AdRequest.Builder().build()

        MyApp.mInstance?.let {
            AppOpenAd.load(
                it,
                adId,
                adRequest,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        showLogsAppOpen("$tag app open ad loaded adId = $adId")
                        Log.d(TAG, "App Open ad loaded: tag = $tag")

                        cachedAppOpenAd = ad
                        isLoadingAppOpen = false

                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                showLogsAppOpen("$tag app open ad dismissed adId = $adId")
                                Log.d(TAG, "App Open ad dismissed: tag = $tag")
                                cachedAppOpenAd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                showLogsAppOpen("$tag app open ad failed to show adId = $adId")
                                Log.e(TAG, "App Open ad failed to show: tag = $tag, error = ${adError.message}")
                                cachedAppOpenAd = null
                            }

                            override fun onAdShowedFullScreenContent() {
                                showLogsAppOpen("$tag app open ad showed adId = $adId")
                                Log.d(TAG, "App Open ad showed: tag = $tag")
                            }

                            override fun onAdImpression() {
                                showLogsAppOpen("$tag app open ad impression adId = $adId")
                                Log.d(TAG, "App Open ad impression: tag = $tag")
                                cachedAppOpenAd = null
                            }
                        }

                        flushAppOpenCallbacks(ad)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        showLogsAppOpen("$tag app open ad failed to load adId = $adId")
                        Log.e(TAG, "App Open ad failed to load: tag = $tag, error = ${error.message}")
                        isLoadingAppOpen = false
                        flushAppOpenCallbacks(null)
                    }
                }
            )
        }
    }

    private fun flushAppOpenCallbacks(ad: AppOpenAd?) {
        appOpenWaitingCallbacks.forEach { it(ad) }
        appOpenWaitingCallbacks.clear()
    }

    /**
     * Load a Native Ad (single instance)
     */
    fun loadNativeAd(
        adId: String,
        tag: String,
        callback: (NativeAd?) -> Unit
    ) {
        if (MyApp.mInstance?.isPurchased == true){
            callback(null)
            return
        }
        showLogsNative("$tag native ad loading adId = $adId")
        Log.d(TAG, "loadNativeAd: tag = $tag, adId = $adId")

        // Case 1: Already have cached ad
        cachedNativeAd?.let {
            showLogsNative("$tag native ad already cached")
            Log.d(TAG, "Native ad already cached")
            callback(it)
            return
        }

        // Case 2: Already loading → enqueue callback
        if (isLoadingNative) {
            showLogsNative("$tag native ad already loading, enqueueing callback")
            Log.d(TAG, "Native ad already loading, enqueueing callback")
            nativeWaitingCallbacks.add(callback)
            return
        }

        // Case 3: Start a new load
        isLoadingNative = true
        nativeWaitingCallbacks.add(callback)

        MyApp.mInstance?.let {
            val adLoader = AdLoader.Builder(it, adId)
                .forNativeAd { ad ->
                    Log.d(TAG, "Native ad loaded: tag = $tag")
                    cachedNativeAd = ad
                    isLoadingNative = false
                    flushNativeCallbacks(ad)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        showLogsNative("$tag native ad failed to load adId = $adId")
                        Log.e(TAG, "Native ad failed to load: tag = $tag, error = ${error.message}")
                        isLoadingNative = false
                        flushNativeCallbacks(null)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        showLogsNative("$tag native ad loaded adId = $adId")
                        Log.d(TAG, "Native ad onAdLoaded: tag = $tag")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        showLogsNative("$tag native ad impression adId = $adId")
                        Log.d(TAG, "Native ad impression: tag = $tag")
                        cachedNativeAd = null
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build())
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private fun flushNativeCallbacks(ad: NativeAd?) {
        nativeWaitingCallbacks.forEach { it(ad) }
        nativeWaitingCallbacks.clear()
    }

    /**
     * Load an Adaptive Banner Ad (single instance with caching)
     * @param activity The activity context
     * @param adId The ad unit ID
     * @param tag Tag for logging purposes
     * @param callback Returns the loaded AdView or null if failed
     */
    fun loadAdaptiveBanner(
        activity: Activity,
        adId: String,
        tag: String,
        callback: (AdView?) -> Unit
    ) {
        showLogsBanner("$tag adaptive banner loading adId = $adId")
        Log.d(TAG, "loadAdaptiveBanner: tag = $tag, adId = $adId")

        // Case 1: Already have cached banner
        cachedBannerAdView?.let {
            showLogsBanner("$tag adaptive banner already cached")
            Log.d(TAG, "Adaptive banner already cached")
            callback(it)
            return
        }

        // Case 2: Already loading → enqueue callback
        if (isLoadingBanner) {
            showLogsBanner("$tag adaptive banner already loading, enqueueing callback")
            Log.d(TAG, "Adaptive banner already loading, enqueueing callback")
            bannerWaitingCallbacks.add(callback)
            return
        }

        // Case 3: Start a new load
        isLoadingBanner = true
        bannerWaitingCallbacks.add(callback)

        // Create AdView
        val adView = AdView(activity)
        adView.adUnitId = adId

        // Get adaptive ad size
        val adSize = getAdaptiveBannerSize(activity)
        adView.setAdSize(adSize)

        // Set ad listener for caching and callbacks
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                showLogsBanner("$tag adaptive banner loaded adId = $adId")
                Log.d(TAG, "Adaptive banner loaded: tag = $tag")

                cachedBannerAdView = adView
                isLoadingBanner = false
                flushBannerCallbacks(adView)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                showLogsBanner("$tag adaptive banner failed to load adId = $adId, error = ${error.message}")
                Log.e(TAG, "Adaptive banner failed to load: tag = $tag, error = ${error.message}")

                isLoadingBanner = false
                flushBannerCallbacks(null)
            }

            override fun onAdOpened() {
                super.onAdOpened()
                showLogsBanner("$tag adaptive banner opened adId = $adId")
                Log.d(TAG, "Adaptive banner opened: tag = $tag")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                showLogsBanner("$tag adaptive banner closed adId = $adId")
                Log.d(TAG, "Adaptive banner closed: tag = $tag")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                cachedBannerAdView = null
                showLogsBanner("$tag adaptive banner impression adId = $adId")
                Log.d(TAG, "Adaptive banner impression: tag = $tag")
            }
        }

        // Load the ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun flushBannerCallbacks(adView: AdView?) {
        bannerWaitingCallbacks.forEach { it(adView) }
        bannerWaitingCallbacks.clear()
    }

    /**
     * Get adaptive banner size based on screen width
     */
    private fun getAdaptiveBannerSize(activity: Activity): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density
        val adWidthPixels = outMetrics.widthPixels.toFloat()

        // If the banner is being placed in a smaller container, use the container width
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    /**
     * Load a collapsible banner with caching (alternative method with collapse option)
     * @param activity The activity context
     * @param adId The ad unit ID
     * @param tag Tag for logging purposes
     * @param collapsePosition "top" or "bottom" for collapsible banner
     * @param callback Returns the loaded AdView or null if failed
     */
    fun loadCollapsibleBanner(
        activity: Activity,
        adId: String,
        tag: String,
        collapsePosition: String = "bottom",
        callback: (AdView?) -> Unit
    ) {
        showLogsBanner("$tag collapsible banner loading adId = $adId, position = $collapsePosition")
        Log.d(TAG, "loadCollapsibleBanner: tag = $tag, adId = $adId, position = $collapsePosition")

        // Case 1: Already have cached banner
        cachedBannerAdView?.let {
            showLogsBanner("$tag collapsible banner already cached")
            Log.d(TAG, "Collapsible banner already cached")
            callback(it)
            return
        }

        // Case 2: Already loading → enqueue callback
        if (isLoadingBanner) {
            showLogsBanner("$tag collapsible banner already loading, enqueueing callback")
            Log.d(TAG, "Collapsible banner already loading, enqueueing callback")
            bannerWaitingCallbacks.add(callback)
            return
        }

        // Case 3: Start a new load
        isLoadingBanner = true
        bannerWaitingCallbacks.add(callback)

        val adView = AdView(activity)
        adView.adUnitId = adId

        val adSize = getAdaptiveBannerSize(activity)
        adView.setAdSize(adSize)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                showLogsBanner("$tag collapsible banner loaded adId = $adId")
                Log.d(TAG, "Collapsible banner loaded: tag = $tag")

                cachedBannerAdView = adView
                isLoadingBanner = false
                flushBannerCallbacks(adView)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                showLogsBanner("$tag collapsible banner failed to load adId = $adId, error = ${error.message}")
                Log.e(TAG, "Collapsible banner failed to load: tag = $tag, error = ${error.message}")

                isLoadingBanner = false
                flushBannerCallbacks(null)
            }

            override fun onAdImpression() {
                super.onAdImpression()
                showLogsBanner("$tag collapsible banner impression adId = $adId")
                Log.d(TAG, "Collapsible banner impression: tag = $tag")
            }
        }

        // Add collapsible parameter
        val extras = Bundle()
        extras.putString("collapsible", collapsePosition)

        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()

        adView.loadAd(adRequest)
    }

    /**
     * Clear the cached interstitial ad
     */
    fun clearInterstitialCache() {
        cachedInterstitialAd = null
        Log.d(TAG, "Cleared interstitial cache")
    }

    /**
     * Clear the cached app open ad
     */
    fun clearAppOpenCache() {
        cachedAppOpenAd = null
        Log.d(TAG, "Cleared app open cache")
    }

    /**
     * Clear the cached native ad
     */
    fun clearNativeCache() {
        cachedNativeAd = null
        Log.d(TAG, "Cleared native cache")
    }

    /**
     * Clear the cached banner ad
     */
    fun clearBannerCache() {
        cachedBannerAdView?.destroy()
        cachedBannerAdView = null
        Log.d(TAG, "Cleared banner cache")
    }

    /**
     * Clear all caches
     */
    fun clearAllCaches() {
        cachedInterstitialAd = null
        cachedAppOpenAd = null
        cachedNativeAd = null
        cachedBannerAdView?.destroy()
        cachedBannerAdView = null
        Log.d(TAG, "Cleared all ad caches")
    }

    /**
     * Check if an interstitial ad is cached
     */
    fun isInterstitialCached(): Boolean {
        return cachedInterstitialAd != null
    }

    /**
     * Check if an app open ad is cached
     */
    fun isAppOpenCached(): Boolean {
        return cachedAppOpenAd != null
    }

    /**
     * Check if a native ad is cached
     */
    fun isNativeCached(): Boolean {
        return cachedNativeAd != null
    }

    /**
     * Check if a banner ad is cached
     */
    fun isBannerCached(): Boolean {
        return cachedBannerAdView != null
    }

    /**
     * Get the cached interstitial ad
     */
    fun getCachedInterstitialAd(): InterstitialAd? {
        return cachedInterstitialAd
    }

    /**
     * Get the cached app open ad
     */
    fun getCachedAppOpenAd(): AppOpenAd? {
        return cachedAppOpenAd
    }

    /**
     * Get the cached native ad
     */
    fun getCachedNativeAd(): NativeAd? {
        return cachedNativeAd
    }

    /**
     * Get the cached banner ad
     */
    fun getCachedBannerAdView(): AdView? {
        return cachedBannerAdView
    }
}