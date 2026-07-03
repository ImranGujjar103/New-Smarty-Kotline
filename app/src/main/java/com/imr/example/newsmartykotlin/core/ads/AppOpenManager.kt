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
import com.imr.example.newsmartykotlin.core.extensions.isActivityAlive
import com.imr.example.newsmartykotlin.core.extensions.isInternetAvailable
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

    private var dialog: Dialog? = null

    private var isRestricted = false

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
              //  loadAd(MyApp.CONTEXT)
            } catch (e: Exception) {
                Log.e(TAG, "Error showing ad: ${e.message}")
            }
        }
    }

    private fun shouldShowAd(): Boolean {
        if (application.isPurchased) return false
        if (!application.appConfig.appOpenResume.toShow) return false
        if (isRestricted) return false

        val activity = currentActivity ?: return false

        return true
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val timeSinceLoad = Date().time - loadTime
        return timeSinceLoad < 4 * 60 * 60 * 1000
    }



    private fun loadAd(context: Context) {
        if (!context.isInternetAvailable()) return
        if (isLoadingAd || isAdAvailable()) return
/*        Log.d("appExitTest", "isAppExit : $isAppExit")
        if(application.isAppExit) return*/

        if (!shouldShowAd() || currentActivity == null || isShowingAd) return

        Log.d(TAG, "Loading App Open Ad...")
        isLoadingAd = true


        Log.d("ad_log_openapp", "⏳ Resume Open App Loading")
        val adId = application.appConfig.appOpenResume.adId

        Handler(Looper.getMainLooper()).post {
            val request = AdRequest.Builder(adId).build()
            Log.d(TAG, "adId : $adId")
            AppOpenAd.load(request, object : AdLoadCallback<AppOpenAd> {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(TAG, "App Open Ad loaded.")
                    Log.d("ad_log_openapp", "✅ Resume Open App Loaded")

                    showAdIfAvailable(currentActivity)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingAd = false
                    Log.d(TAG, "Failed to load App Open Ad: ${error.message}")
                    Log.d("ad_log_openapp", "❌ Resume Open App Failed")
                }
            })
        }
    }

    private fun showAdIfAvailable(activity: Activity?) {
        if (!shouldShowAd() || activity == null || isShowingAd) return

        Log.d("openAppTest","isAdAvailable : ${isAdAvailable()}")

        Handler(Looper.getMainLooper()).post {
            activity.isActivityAlive {
                dialog?.dismiss()
            }
        }

        if (!isAdAvailable()) {
            loadAd(activity)
            return
        }

        showFullScreenDialog(activity)  // Show loading screen

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            appOpenAd?.adEventCallback = object : AppOpenAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false

                    activity.isActivityAlive {
                        dialog?.dismiss()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                    isShowingAd = false
                    Log.d("ad_log_openapp", "onAdFailedToShowFullScreenContent : ${fullScreenContentError.message}")
                    activity.isActivityAlive {
                        dialog?.dismiss()
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    Log.d(TAG, "App Open Ad shown.")
                    Log.d("ad_log_openapp", "✨ Resume Open App Impression")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "App Open Ad clicked.")
                }

                override fun onAdImpression() {
                    Log.d(TAG, "App Open Ad impression.")
                    appOpenAd = null
                }
            }

            try {
                isShowingAd = true
                appOpenAd?.show(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Exception showing ad: ${e.message}")
                activity.isActivityAlive {
                    dialog?.dismiss()
                }
            }
        }

        handler?.let { h ->
            runnable?.let { r ->
                h.postDelayed(r, 500)
            }
        }
    }

    private fun safeDismissDialog() {
        dialog?.let {
            val ctx = it.context
            if (it.isShowing && ctx is Activity && !ctx.isFinishing && !ctx.isDestroyed) {
                it.dismiss()
            }
        }
        dialog = null
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


    private fun showFullScreenDialog(activity: Activity) {
     /*   dialog = Dialog(activity, R.style.Theme_AntiTheftApp)
        dialog?.apply {
            setContentView(R.layout.dialog_full_screen)

            setCancelable(false)
            window?.hideNavigationBar()

            window?.setBackgroundDrawableResource(android.R.color.white)
            show()
        }*/
    }

    private fun clearAd() {
        Log.d(TAG, "Clearing AppOpenAd reference")
        appOpenAd = null
        isLoadingAd = false
        isShowingAd = false
        loadTime = 0L
        handler?.removeCallbacks(runnable ?: Runnable { })
        currentActivity?.isActivityAlive {
            dialog?.dismiss()
        }
    }
}