package com.imr.example.newsmartykotlin

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imr.example.newsmartykotlin.core.ads.AppOpenManager
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.data.model.AppConfig
import com.imr.example.newsmartykotlin.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {


    var ctrBTNColor: String = "#1e9956"
    var appConfig: AppConfig = AppConfig()

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var CONTEXT: Context
        var mInstance: MyApp? = null

        var appOpenManager: AppOpenManager? = null

    }
    var firebaseAnalytics: FirebaseAnalytics? = null
    var isPurchased = false
    private val dataStorePrefs : DataStorePrefs by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        CONTEXT = this
        mInstance = this
        appOpenManager=AppOpenManager(this)

        FirebaseApp.initializeApp(this@MyApp)

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled =
            BuildConfig.DEBUG.not()
        firebaseAnalytics = CONTEXT.let { FirebaseAnalytics.getInstance(it) }
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }


        CoroutineScope(Dispatchers.Main).launch{
            // Now dataStorePrefs can be safely accessed
            dataStorePrefs.getIsPurchased().collect {
                isPurchased = it
                Log.d("cvv", "isPurchased: $it")
            }

        }

    }
}