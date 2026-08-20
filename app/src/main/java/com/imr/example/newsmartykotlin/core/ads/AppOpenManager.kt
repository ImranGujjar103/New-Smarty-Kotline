package com.imr.example.newsmartykotlin.core.ads

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdActivity
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError

import com.imr.example.newsmartykotlin.MyApp
import com.imr.example.newsmartykotlin.core.extensions.hideNavigationBar
import com.imr.example.newsmartykotlin.core.extensions.isInternetAvailable
import com.imr.example.newsmartykotlin.core.extensions.showLogsAppOpen
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
class AppOpenManager(
    private val application: MyApp
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0L
    private var currentActivity: Activity? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var isRestricted = true
    private var tag = "appOpenResume"

    fun setIsRestricted(restricted: Boolean) {
        isRestricted = restricted
        Log.d(TAG, "setIsRestricted: $isRestricted")
    }


    companion object {
        private const val TAG = "AppOpenManager"
        private var appOpenAd: AppOpenAd? = null
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {

        if (shouldShowAd()) {
            try {
                showAdIfAvailable(currentActivity)
            } catch (e: Exception) {
                showLogsAppOpen("Error showing ad in onStart: ${e.message}")
            }
        }
    }

    private fun shouldShowAd(): Boolean {
        if (application.isPurchased) {
            showLogsAppOpen(" $tag ---->  User is premium")
            return false
        }
        val config = application.appConfig.appOpenResume
        if (isRestricted) {
            return false
        }
        if (!config.toShow) {
            showLogsAppOpen(" $tag ---->  Remote config toShow is false")
            return false
        }
        if (config.adId.isEmpty()) {
            showLogsAppOpen(" $tag ---->   - Ad ID is empty")
            return false
        }


        val activity = currentActivity ?: run {
            showLogsAppOpen(" $tag ---->    - Current activity is null")
            return false
        }
        if (activity is AdActivity) {
            showLogsAppOpen(" $tag ---->   - Current activity is AdActivity")
            return false
        }
        
        // Prevent showing on top of other full screen ads
        if (AdLoadingState.isInterstitialShowing.value) {
            showLogsAppOpen(" $tag ---->    - Another interstitial/full-screen ad is showing")
            return false
        }

        if (AdLoadingState.isShowing.value && !isLoadingAd && !isShowingAd) {
            showLogsAppOpen(" $tag ---->    - Ad loading overlay is active (another ad is loading)")
            return false
        }

        return true
    }

    private fun isAdAvailable(): Boolean {
        val available = appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
        showLogsAppOpen("isAdAvailable: $available")
        return available
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val timeSinceLoad = Date().time - loadTime
        return timeSinceLoad < 4 * 60 * 60 * 1000
    }

    private fun loadAd(context: Context) {
        if (!context.isInternetAvailable()) {
            showLogsAppOpen(" $tag ---->   : Internet not available")
            return
        }
        if (isLoadingAd) {
            showLogsAppOpen(" $tag ---->   : Already loading...")
            return
        }

        if (!shouldShowAd() || currentActivity == null || isShowingAd) {
            showLogsAppOpen(" $tag ---->   : shouldShowAd check failed or already showing")
            return
        }

        showLogsAppOpen(" $tag ---->   : Starting load runtime...")
        isLoadingAd = true
        AdLoadingState.show()

        val adId = application.appConfig.appOpenResume.adId

        Handler(Looper.getMainLooper()).post {
            val request = AdRequest.Builder(adId).build()
            AppOpenAd.load(request, object : AdLoadCallback<AppOpenAd> {
                override fun onAdLoaded(ad: AppOpenAd) {
                    showLogsAppOpen(" $tag ---->   : onAdLoaded")
                    appOpenAd = ad
                    loadTime = Date().time
                    showAdIfAvailable(currentActivity)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    showLogsAppOpen(" $tag ---->   : onAdFailedToLoad: ${error.message}")
                    isLoadingAd = false
                    AdLoadingState.hide()
                }
            })
        }
    }

    private fun showAdIfAvailable(activity: Activity?) {
        if (!shouldShowAd() || activity == null || isShowingAd) {
            if (isLoadingAd) {
                isLoadingAd = false
                AdLoadingState.hide()
            }
            return
        }

        if (!isAdAvailable()) {
            showLogsAppOpen(" $tag ---->   : Ad not available, loading...")
            loadAd(activity)
            return
        }

        showLogsAppOpen(" $tag ---->   : Ad is available, posting show runnable")
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            isLoadingAd = false
            appOpenAd?.adEventCallback = object : AppOpenAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    showLogsAppOpen(" $tag ---->   onAdDismissedFullScreenContent")
                    appOpenAd = null
                    isShowingAd = false
                    AdLoadingState.hide()
                    AdLoadingState.setInterstitialShowing(false)
                    AdLoadingState.setAdDismissed(true)
                    
                    // Small delay to reset dismissal state
                    Handler(Looper.getMainLooper()).postDelayed({
                        AdLoadingState.setAdDismissed(false)
                    }, 600)
                }

                override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                    showLogsAppOpen(" $tag ---->   onAdFailedToShowFullScreenContent: ${fullScreenContentError.message}")
                    isShowingAd = false
                    appOpenAd = null
                    AdLoadingState.hide()
                    AdLoadingState.setInterstitialShowing(false)
                }

                override fun onAdShowedFullScreenContent() {
                    showLogsAppOpen(" $tag ---->   onAdShowedFullScreenContent")
                    isShowingAd = true
                    AdLoadingState.setInterstitialShowing(true)
                }

                override fun onAdClicked() {
                    showLogsAppOpen(" $tag ---->   onAdClicked")
                }

                override fun onAdImpression() {
                    showLogsAppOpen(" $tag ---->   onAdImpression")
                    appOpenAd = null
                }
            }

            try {
                isShowingAd = true
                showLogsAppOpen(" $tag ---->   Calling appOpenAd?.show")
                appOpenAd?.show(activity)
            } catch (e: Exception) {
                showLogsAppOpen(" $tag ---->   Exception showing ad: ${e.message}")
                isShowingAd = false
                AdLoadingState.hide()
                AdLoadingState.setInterstitialShowing(false)
            }
        }

        handler?.let { h ->
            runnable?.let { r ->
                h.postDelayed(r, 500)
            }
        }
    }

    private fun safeDismissDialog() {
        // No longer using dialog, using AdLoadingState instead
    }

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
        if (activity is AdActivity){
            activity.hideNavigationBar()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
           // clearAd()
            currentActivity = null
        }
    }


    private fun clearAd() {
        Log.d(TAG, "Clearing AppOpenAd reference")
        appOpenAd = null
        isLoadingAd = false
        isShowingAd = false
        loadTime = 0L
        handler?.removeCallbacks(runnable ?: Runnable { })
        AdLoadingState.hide()
    }
}
