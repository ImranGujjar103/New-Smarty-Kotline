package com.imr.example.newsmartykotlin.core.extensions

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.AnimRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.imr.example.newsmartykotlin.BuildConfig
import com.imr.example.newsmartykotlin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun View.visible() {
    this.visibility = View.VISIBLE
}


fun View.gone() {
    this.visibility = View.GONE
}
fun View.inVisible() {
    this.visibility = View.INVISIBLE
}
/*fun Fragment.showToast(message: String, type: ToastType, duration: Int = FancyToast.LENGTH_SHORT) {
    isAlive {
        FancyToast.makeText(it, message, duration, type.value, false).show()
    }

}*/

fun View.startCustomAnimation(@AnimRes animRes: Int) {
    val animation = AnimationUtils.loadAnimation(context, animRes)
    this.startAnimation(animation)
}

@Composable
fun Modifier.heartbeatAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    return this.scale(scale)
}
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)

    return capabilities != null && (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||           // Wi-Fi
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||       // Cellular
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||       // Ethernet
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||      // Bluetooth
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||            // Virtual Private Network (VPN)
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) ||     // Wi-Fi Aware
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) ||         // Low-Power and Lossy Network (LoWPAN)
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB)            // USB connection

            )
}

/*fun Int.toNativeAdLayout(): Int {
    return when (this) {
        0 -> R.layout.native_ad_medium
        1 -> R.layout.native_ad_medium
        2 -> R.layout.native_ad_without_media
        else -> R.layout.native_ad_medium // or View.GONE marker
    }
}*/

/*fun Int.toSmallNativeAdLayout(): Int {
    return when (this) {
        0 -> R.layout.native_ad_small
        1 -> R.layout.native_ad_small
        2 -> R.layout.native_ad_small
        else -> R.layout.native_ad_small // or View.GONE marker
    }
}*/



fun Activity.hideNavigationBar() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).apply {
        hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
fun Window.hideNavigationBar() {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    WindowInsetsControllerCompat(this, this.decorView).apply {
        hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

//fun Activity.restoreSystemBars() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        window.setDecorFitsSystemWindows(false)
//        window.insetsController?.show(WindowInsets.Type.systemBars())
//    } else {
//        @Suppress("DEPRECATION")
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//    }
//}


fun Activity.enableFullScreenWithBottomBar(targetView: View) {
    restoreSystemBars()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.insetsController?.let { controller ->
            // Only hide status bar, keep navigation bar visible
            controller.hide(WindowInsets.Type.statusBars())
            controller.show(WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Handle window insets to avoid content overlap
        targetView.setOnApplyWindowInsetsListener { view, insets ->
            val topInset = insets.getInsets(WindowInsets.Type.statusBars()).top
            val bottomInset = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
            view.setPadding(0, 0, 0, bottomInset)
            insets
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Handle insets for older Android versions
        targetView.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
    }
}
fun Activity.restoreSystemBars() {
    WindowCompat.getInsetsController(window, window.decorView).show(
        WindowInsetsCompat.Type.systemBars()
    )
}

//fun Activity.enableEdgeToEdgeWithInsets(targetView: View,color: Int = R.color.theme,showDarkIcons:Boolean=false) {
//
//
//    restoreSystemBars()
//    when {
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
//            window.setDecorFitsSystemWindows(false)
//
//            window.insetsController?.apply {
//                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
//
//            }
//
//            window.statusBarColor = ContextCompat.getColor(this, color)
//            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
//
//            window.attributes.layoutInDisplayCutoutMode =
//                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//
//            targetView.setOnApplyWindowInsetsListener { view, insets ->
//                val topInset = insets.getInsets(WindowInsets.Type.statusBars()).top
//                val bottomInset = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
//                view.setPadding(0, topInset, 0, bottomInset)
//                insets
//            }
//        }
//
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
//            window.setDecorFitsSystemWindows(false)
//
//            window.insetsController?.apply {
//                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
//            }
//
//            window.attributes.layoutInDisplayCutoutMode =
//                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//
//            window.statusBarColor = ContextCompat.getColor(this, color)
//            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
//
//            targetView.setOnApplyWindowInsetsListener { view, insets ->
//                val topInset = insets.getInsets(WindowInsets.Type.statusBars()).top
//                val bottomInset = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
//                view.setPadding(0, topInset, 0, bottomInset)
//                insets
//            }
//        }
//
//        else -> {
//
//
//            window.statusBarColor = ContextCompat.getColor(this,color)
//            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
//        }
//    }
//}



 fun Activity.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission)  == PackageManager.PERMISSION_GRANTED
}


private fun Activity.setStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window?.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }
}





fun Activity.enableFullScreenMode() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
//        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }
}


fun Context.hasPermissions(permissions: Array<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}


inline fun <reified T : Service> Context.isServiceRunning(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION")
    val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

    return runningServices.any { it.service.className == T::class.java.name }
}



fun FragmentActivity.requestPermission(
    permission: String,
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onShowRationale: (() -> Unit)? = null
) {

    when {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
            onGranted()
        }

        ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
            // ✅ Rationale should be shown
            onShowRationale?.invoke()
        }

        else -> {
            // ✅ Directly request permission
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    onGranted()
                } else {
                    onDenied?.invoke()
                }
            }
            launcher.launch(permission)
        }
    }
}










fun Fragment.isLive(): Boolean {
    return isAdded && !isDetached
}





fun postDelayed(delayMillis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ action() }, delayMillis)
}


fun Activity.sendAnalytics(actionName: String, actionDetail: String) {
    val firebaseAnalytics = FirebaseAnalytics.getInstance(this)


    if(BuildConfig.DEBUG){
        runOnUiThread {
            Log.d("eventTest","$actionName - $actionDetail")
          //  Toast.makeText(this, "Name: $actionName - Detail: $actionDetail", Toast.LENGTH_SHORT).show()
        }
    }


    val bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.CONTENT_TYPE, actionName)
        putString("ACTION_TYPE", actionDetail)
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            firebaseAnalytics.logEvent(actionDetail, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


fun Dialog.applyDialogWindow(marginDp: Int = 24) {
    val marginPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        marginDp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

    window?.apply {
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val background = Color.TRANSPARENT.toDrawable()
        val inset = InsetDrawable(background, marginPx, 0, marginPx, 0)
        setBackgroundDrawable(inset)
    }
}

/**
 * Apply standard dialog window styling for AlertDialog
 */
fun AlertDialog.applyDialogWindow(marginDp: Int = 24) {
    val marginPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        marginDp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

    window?.apply {
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val background = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(background, marginPx, 0, marginPx, 0)
        setBackgroundDrawable(inset)
    }
}


fun View.preventDoubleClick() {

    try {
        this.isEnabled = false
        this.postDelayed({ this.isEnabled = true }, 1000)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

}

fun Fragment.onBackPress(onPress: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onPress()
        }
    })
}


fun Fragment.isAlive(callback: (Activity) -> Unit) {
    if (activity != null && isAdded && !isDetached) {
        activity?.let { it.isActivityAlive { mainActivity -> callback(mainActivity) } }
    }
}

fun Activity.isActivityAlive(callback: (Activity) -> Unit) {
    try {
        if (isFinishing.not() &&
            isDestroyed.not()
        ) {
            callback(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


