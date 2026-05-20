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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
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
      //  if(isAppExit) return false
        if (application.isPurchased) return false
        if (!application.appConfig.appOpenResume.toShow) return false

        val activity = currentActivity as? FragmentActivity ?: return false

        // Try to find the nav host fragment safely
 /*       val navHostView = activity.findViewById<View?>(R.id.nav_host_fragment)
        if (navHostView == null) {
            // No nav host found — probably an activity that doesn't use navigation
            Log.w(TAG, "No nav host found in ${activity.localClassName}, skipping ad.")
            return false
        }*/

        /*return try {
            val navController = Navigation.findNavController(navHostView)
            navController.currentDestination?.id != R.id.splashFragment
        } catch (e: Exception) {
            Log.e(TAG, "Error finding NavController: ${e.message}")
            false
        }*/
        return false
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
        val request = AdManagerAdRequest.Builder().build()
        val adId = application.appConfig.appOpenResume.adId
        Log.d(TAG, "adId : $adId")
        AppOpenAd.load(context, adId, request, object : AppOpenAd.AppOpenAdLoadCallback() {
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

    private fun showAdIfAvailable(activity: Activity?) {
        if (!shouldShowAd() || activity == null || isShowingAd) return

        Log.d("openAppTest","isAdAvailable : ${isAdAvailable()}")

        activity.isActivityAlive {
            dialog?.dismiss()
        }

        if (!isAdAvailable()) {
            loadAd(activity)
            return
        }

        showFullScreenDialog(activity)  // Show loading screen

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                  //  loadAd(activity)

                    activity.isActivityAlive {
                        dialog?.dismiss()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                 //   appOpenAd = null
                    isShowingAd = false
                    Log.d("ad_log_openapp", "onAdFailedToShowFullScreenContent : ${adError.message}")
                   // loadAd(activity)
                    activity.isActivityAlive {
                        dialog?.dismiss()
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    Log.d(TAG, "App Open Ad shown.")


                    Log.d("ad_log_openapp", "✨ Resume Open App Impression")

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

        handler?.postDelayed(runnable!!, 500)
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

        if (activity is AdActivity) {
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