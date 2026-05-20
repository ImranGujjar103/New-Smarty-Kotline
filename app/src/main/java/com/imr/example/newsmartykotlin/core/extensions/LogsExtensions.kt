package com.imr.example.newsmartykotlin.core.extensions

import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showLogsBanner(message: String) {
    Log.i("banner_ad_log", message)

}
fun showLogsNative(message: String) {
    Log.i("native_ad_log", message)

}

fun showLogsRewarded(message: String) {
    Log.i("rewarded_ad_log", message)

}

fun showLogsAppOpen(message: String) {
    Log.i("app_open_ad_log", message)
}

fun showLogsInter(message: String) {

    Log.i("interstitial_ad_log", message)
}
    fun sendEvent(actionDetail: String) {
        Log.e("EventLog", "actionDetail:  $actionDetail")
      //  Log.e("EventLog", "actionName:  $actionNam")
//        var actionName = actionName.replace("}", "")
//        actionName = actionName.replace("{", "")
        val bundle = Bundle()
   //     bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, actionDetail)
        bundle.putString("ACTION_TYPE", "actionName")
        CoroutineScope(Dispatchers.IO).launch {
          // newsmartykotlinApp.mInstance?.firebaseAnalytics?.logEvent(actionDetail, bundle)
        }
    }
