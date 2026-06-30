package com.imr.example.newsmartykotlin.core.ads

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdChoicesPlacement
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.VideoOptions
import com.imr.example.newsmartykotlin.MyApp
import com.imr.example.newsmartykotlin.core.extensions.showLogsAppOpen
import com.imr.example.newsmartykotlin.core.extensions.showLogsBanner
import com.imr.example.newsmartykotlin.core.extensions.showLogsInter
import com.imr.example.newsmartykotlin.core.extensions.showLogsNative
import kotlinx.coroutines.CoroutineScope

object AdManager {
    private const val TAG = "AdManager"
    const val AD_SETTING_RELEASE = "ad_monetization_release"
    const val AD_SETTING_DEBUG = "ad_strategy_debug"

    private var cachedInterstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false
    private val interstitialWaitingCallbacks = mutableListOf<(InterstitialAd?) -> Unit>()

    private var cachedAppOpenAd: AppOpenAd? = null
    private var isLoadingAppOpen = false
    private val appOpenWaitingCallbacks = mutableListOf<(AppOpenAd?) -> Unit>()

    private val nativeAdCache = mutableMapOf<String, NativeAd>()
    private val isLoadingNativeMap = mutableMapOf<String, Boolean>()
    private val nativeWaitingCallbacksMap = mutableMapOf<String, MutableList<(NativeAd?) -> Unit>>()

    private var cachedBannerAdView: AdView? = null
    private var isLoadingBanner = false
    private val bannerWaitingCallbacks = mutableListOf<(AdView?) -> Unit>()

    fun loadInterstitialAd(
        adId: String,
        tag: String,
        callback: (InterstitialAd?) -> Unit
    ) {
        if (MyApp.mInstance?.isPurchased == true) {
            callback(null)
            return
        }

        showLogsInter("$tag interstitial ad loading adId = $adId")
        Log.d(TAG, "loadInterstitialAd: tag = $tag, adId = $adId")

        cachedInterstitialAd?.let {
            showLogsInter("$tag interstitial ad already cached")
            callback(it)
            return
        }

        if (isLoadingInterstitial) {
            showLogsInter("$tag interstitial ad already loading, enqueueing callback")
            interstitialWaitingCallbacks.add(callback)
            return
        }

        isLoadingInterstitial = true
        interstitialWaitingCallbacks.add(callback)

        Handler(Looper.getMainLooper()).post {
            val adRequest = AdRequest.Builder(adId).build()

            InterstitialAd.load(
                adRequest,
                object : AdLoadCallback<InterstitialAd> {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        showLogsInter("$tag interstitial ad loaded adId = $adId")
                        cachedInterstitialAd = ad
                        isLoadingInterstitial = false
                        flushInterstitialCallbacks(ad)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        showLogsInter("$tag interstitial ad failed to load: ${adError.message}")
                        isLoadingInterstitial = false
                        flushInterstitialCallbacks(null)
                    }
                }
            )
        }
    }

    private fun flushInterstitialCallbacks(ad: InterstitialAd?) {
        Handler(Looper.getMainLooper()).post {
            interstitialWaitingCallbacks.forEach { it(ad) }
            interstitialWaitingCallbacks.clear()
        }
    }

    fun showInterstitialAd(
        activity: Activity,
        adLoadingController: AdLoadingController,
        scope: CoroutineScope,
        callback: () -> Unit
    ) {
        cachedInterstitialAd?.let { ad ->
            adLoadingController.show(scope = scope) {
                ad.adEventCallback = object : InterstitialAdEventCallback {
                    override fun onAdDismissedFullScreenContent() {
                        Handler(Looper.getMainLooper()).post {
                            AdLoadingState.setInterstitialShowing(false)
                            cachedInterstitialAd = null
                            callback()
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                        Handler(Looper.getMainLooper()).post {
                            AdLoadingState.setInterstitialShowing(false)
                            cachedInterstitialAd = null
                            callback()
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        AdLoadingState.setInterstitialShowing(true)
                    }

                    override fun onAdImpression() {
                        Handler(Looper.getMainLooper()).post {
                            cachedInterstitialAd = null
                        }
                    }
                }
                ad.show(activity)
            }
        } ?: run {
            callback()
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
            adLoadingController.show(scope = scope) {
                ad.adEventCallback = object : InterstitialAdEventCallback {
                    override fun onAdDismissedFullScreenContent() {
                        Handler(Looper.getMainLooper()).post {
                            AdLoadingState.hide()
                            AdLoadingState.setInterstitialShowing(false)
                            showLogsInter("$tag interstitial ad dismissed")
                            cachedInterstitialAd = null
                            callback()
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                        Handler(Looper.getMainLooper()).post {
                            AdLoadingState.hide()
                            AdLoadingState.setInterstitialShowing(false)
                            showLogsInter("$tag interstitial ad failed to show: ${fullScreenContentError.message}")
                            cachedInterstitialAd = null
                            callback()
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        AdLoadingState.setInterstitialShowing(true)
                        showLogsInter("$tag interstitial ad showed")
                    }

                    override fun onAdClicked() {
                        showLogsInter("$tag interstitial ad clicked")
                    }

                    override fun onAdImpression() {
                        Handler(Looper.getMainLooper()).post {
                            showLogsInter("$tag interstitial ad impression")
                            cachedInterstitialAd = null
                        }
                    }
                }
                ad.show(activity)
            }
        } ?: run {
            callback()
        }
    }

    fun loadAppOpenAd(
        adId: String,
        tag: String,
        callback: (AppOpenAd?) -> Unit
    ) {
        if (MyApp.mInstance?.isPurchased == true) {
            callback(null)
            return
        }

        showLogsAppOpen("$tag app open ad loading adId = $adId")
        Log.d(TAG, "loadAppOpenAd: tag = $tag, adId = $adId")

        cachedAppOpenAd?.let {
            showLogsAppOpen("$tag app open ad already cached")
            callback(it)
            return
        }

        if (isLoadingAppOpen) {
            showLogsAppOpen("$tag app open ad already loading, enqueueing callback")
            appOpenWaitingCallbacks.add(callback)
            return
        }

        isLoadingAppOpen = true
        appOpenWaitingCallbacks.add(callback)

        Handler(Looper.getMainLooper()).post {
            val adRequest = AdRequest.Builder(adId).build()

            AppOpenAd.load(
                adRequest,
                object : AdLoadCallback<AppOpenAd> {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        showLogsAppOpen("$tag app open ad loaded")
                        cachedAppOpenAd = ad
                        isLoadingAppOpen = false
                        flushAppOpenCallbacks(ad)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        showLogsAppOpen("$tag app open ad failed to load: ${adError.message}")
                        isLoadingAppOpen = false
                        flushAppOpenCallbacks(null)
                    }
                }
            )
        }
    }

    private fun flushAppOpenCallbacks(ad: AppOpenAd?) {
        Handler(Looper.getMainLooper()).post {
            appOpenWaitingCallbacks.forEach { it(ad) }
            appOpenWaitingCallbacks.clear()
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
            adLoadingController.show(scope = scope) {
                ad.adEventCallback = object : AppOpenAdEventCallback {
                    override fun onAdDismissedFullScreenContent() {
                        Handler(Looper.getMainLooper()).post {
                            AdLoadingState.hide()
                            showLogsAppOpen("$tag app open ad dismissed")
                            cachedAppOpenAd = null
                            callback()
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                        Handler(Looper.getMainLooper()).post {
                            AdLoadingState.hide()
                            showLogsAppOpen("$tag app open ad failed to show: ${fullScreenContentError.message}")
                            cachedAppOpenAd = null
                            callback()
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        showLogsAppOpen("$tag app open ad showed")
                    }

                    override fun onAdClicked() {
                        showLogsAppOpen("$tag app open ad clicked")
                    }

                    override fun onAdImpression() {
                        Handler(Looper.getMainLooper()).post {
                            showLogsAppOpen("$tag app open ad impression")
                            cachedAppOpenAd = null
                        }
                    }
                }
                ad.show(activity)
            }
        } ?: run {
            callback()
        }
    }

    fun loadNativeAd(
        adId: String,
        tag: String,
        callback: (NativeAd?) -> Unit
    ) {
        if (MyApp.mInstance?.isPurchased == true) {
            callback(null)
            return
        }

        showLogsNative("$tag native ad loading adId = $adId")
        Log.d(TAG, "loadNativeAd: tag = $tag, adId = $adId")

        nativeAdCache[tag]?.let {
            showLogsNative("$tag native ad already cached")
            callback(it)
            return
        }

        if (isLoadingNativeMap[tag] == true) {
            showLogsNative("$tag native ad already loading, enqueueing callback")
            nativeWaitingCallbacksMap.getOrPut(tag) { mutableListOf() }.add(callback)
            return
        }

        isLoadingNativeMap[tag] = true
        nativeWaitingCallbacksMap.getOrPut(tag) { mutableListOf() }.add(callback)

        Handler(Looper.getMainLooper()).post {
            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adRequest = NativeAdRequest.Builder(adId, listOf(NativeAd.NativeAdType.NATIVE))
                .setVideoOptions(videoOptions)
                .setAdChoicesPlacement(AdChoicesPlacement.TOP_RIGHT)
                .build()

            NativeAdLoader.load(adRequest, object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    showLogsNative("$tag native ad loaded")

                    nativeAd.adEventCallback = object : NativeAdEventCallback {
                        override fun onAdImpression() {
                            Handler(Looper.getMainLooper()).post {
                                showLogsNative("$tag native ad impression")
                            }
                        }
                    }

                    nativeAdCache[tag] = nativeAd
                    isLoadingNativeMap[tag] = false
                    flushNativeCallbacks(tag, nativeAd)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    showLogsNative("$tag native ad failed to load: ${adError.message}")
                    isLoadingNativeMap[tag] = false
                    flushNativeCallbacks(tag, null)
                }
            })
        }
    }

    private fun flushNativeCallbacks(tag: String, ad: NativeAd?) {
        Handler(Looper.getMainLooper()).post {
            nativeWaitingCallbacksMap[tag]?.forEach { it(ad) }
            nativeWaitingCallbacksMap[tag]?.clear()
        }
    }

    fun loadAdaptiveBanner(
        activity: Activity,
        adId: String,
        tag: String,
        callback: (AdView?) -> Unit
    ) {
        if (MyApp.mInstance?.isPurchased == true) {
            callback(null)
            return
        }

        showLogsBanner("$tag adaptive banner loading adId = $adId")
        Log.d(TAG, "loadAdaptiveBanner: tag = $tag, adId = $adId")

        cachedBannerAdView?.let {
            showLogsBanner("$tag adaptive banner already cached")
            callback(it)
            return
        }

        if (isLoadingBanner) {
            showLogsBanner("$tag adaptive banner already loading, enqueueing callback")
            bannerWaitingCallbacks.add(callback)
            return
        }

        isLoadingBanner = true
        bannerWaitingCallbacks.add(callback)

        Handler(Looper.getMainLooper()).post {
            val adSize = getAdaptiveBannerSize(activity)
            val adRequest = BannerAdRequest.Builder(adId, adSize).build()
            val adView = AdView(activity)

            adView.loadAd(adRequest, object : AdLoadCallback<BannerAd> {
                override fun onAdLoaded(ad: BannerAd) {
                    showLogsBanner("$tag adaptive banner loaded")

                    ad.adEventCallback = object : BannerAdEventCallback {
                        override fun onAdImpression() {
                            Handler(Looper.getMainLooper()).post {
                                showLogsBanner("$tag adaptive banner impression")
                                cachedBannerAdView = null
                            }
                        }
                    }

                    cachedBannerAdView = adView
                    isLoadingBanner = false
                    flushBannerCallbacks(adView)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    showLogsBanner("$tag adaptive banner failed to load: ${adError.message}")
                    isLoadingBanner = false
                    flushBannerCallbacks(null)
                }
            })
        }
    }

    private fun flushBannerCallbacks(adView: AdView?) {
        Handler(Looper.getMainLooper()).post {
            bannerWaitingCallbacks.forEach { it(adView) }
            bannerWaitingCallbacks.clear()
        }
    }

    private fun getAdaptiveBannerSize(activity: Activity): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        val adWidthPixels = outMetrics.widthPixels.toFloat()
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getLargeAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    fun loadCollapsibleBanner(
        activity: Activity,
        adId: String,
        tag: String,
        collapsePosition: String = "bottom",
        callback: (AdView?) -> Unit
    ) {
        if (MyApp.mInstance?.isPurchased == true) {
            callback(null)
            return
        }

        showLogsBanner("$tag collapsible banner loading adId = $adId, position = $collapsePosition")
        Log.d(TAG, "loadCollapsibleBanner: tag = $tag, adId = $adId")

        cachedBannerAdView?.let {
            showLogsBanner("$tag collapsible banner already cached")
            callback(it)
            return
        }

        if (isLoadingBanner) {
            showLogsBanner("$tag collapsible banner already loading, enqueueing callback")
            bannerWaitingCallbacks.add(callback)
            return
        }

        isLoadingBanner = true
        bannerWaitingCallbacks.add(callback)

        Handler(Looper.getMainLooper()).post {
            val adSize = getAdaptiveBannerSize(activity)
            val extras = Bundle()
            extras.putString("collapsible", collapsePosition)

            val adRequest = BannerAdRequest.Builder(adId, adSize)
                .setGoogleExtrasBundle(extras)
                .build()

            val adView = AdView(activity)

            adView.loadAd(adRequest, object : AdLoadCallback<BannerAd> {
                override fun onAdLoaded(ad: BannerAd) {
                    showLogsBanner("$tag collapsible banner loaded")

                    ad.adEventCallback = object : BannerAdEventCallback {
                        override fun onAdImpression() {
                            Handler(Looper.getMainLooper()).post {
                                showLogsBanner("$tag collapsible banner impression")
                                cachedBannerAdView = null
                            }
                        }
                    }

                    cachedBannerAdView = adView
                    isLoadingBanner = false
                    flushBannerCallbacks(adView)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    showLogsBanner("$tag collapsible banner failed to load: ${adError.message}")
                    isLoadingBanner = false
                    flushBannerCallbacks(null)
                }
            })
        }
    }

    fun clearInterstitialCache() { cachedInterstitialAd = null }
    fun clearAppOpenCache() { cachedAppOpenAd = null }
    fun clearNativeCache() { nativeAdCache.clear() }
    fun clearBannerCache() { 
        Handler(Looper.getMainLooper()).post {
            cachedBannerAdView?.destroy()
            cachedBannerAdView = null 
        }
    }

    fun clearAllCaches() {
        clearInterstitialCache()
        clearAppOpenCache()
        clearNativeCache()
        clearBannerCache()
    }

    fun isInterstitialCached(): Boolean = cachedInterstitialAd != null
    fun isAppOpenCached(): Boolean = cachedAppOpenAd != null
    fun isNativeCached(tag: String): Boolean = nativeAdCache.containsKey(tag)
    fun isBannerCached(): Boolean = cachedBannerAdView != null

    fun getCachedInterstitialAd(): InterstitialAd? = cachedInterstitialAd
    fun getCachedAppOpenAd(): AppOpenAd? = cachedAppOpenAd
    fun getCachedNativeAd(tag: String): NativeAd? = nativeAdCache[tag]
    fun getCachedBannerAdView(): AdView? = cachedBannerAdView
}
