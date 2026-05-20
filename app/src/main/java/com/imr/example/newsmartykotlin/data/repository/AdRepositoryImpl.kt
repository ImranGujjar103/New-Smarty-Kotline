package com.imr.example.newsmartykotlin.data.repository

import android.util.Log

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.gson.Gson
import com.imr.example.newsmartykotlin.BuildConfig
import com.imr.example.newsmartykotlin.MyApp
import com.imr.example.newsmartykotlin.core.ads.AdManager
import com.imr.example.newsmartykotlin.data.model.AppConfig
import com.imr.example.newsmartykotlin.domain.repository.AdRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.jvm.java

class AdRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig
) : AdRepository {

    private val _appConfig = MutableStateFlow(AppConfig())
    override val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    companion object {
        private const val TAG = "AdRepositoryImpl"
    }

    /**
     * Fetch remote configuration values with Result wrapper for better error handling
     */
    override suspend fun fetchRemoteConfig(): Result<AppConfig> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                try {
                    // 1. Get the right config key

                    remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {

                                Log.d(TAG, "fetchRemoteConfig: task.isSuccessful")
                                val wasUpdated = task.result  // Boolean (true/false)

                                // 2. ✅ Parse the config
                                adSettingsSynced = true
                                var adData: FirebaseRemoteConfigValue? = null
                                if (BuildConfig.DEBUG) {
                                    adData =
                                        remoteConfig.getValue(AdManager.AD_SETTING_DEBUG)

                                    Log.d(TAG, "config-data-for-dubug-is ${adData.asString()}")
                                } else {
                                    adData =
                                        remoteConfig.getValue(AdManager.AD_SETTING_RELEASE)

                                    Log.d(TAG, "config-data-for-release-is ${adData.asString()}")
                                }
                                val adSettings =
                                    Gson().fromJson(adData.asString(), AppConfig::class.java)
                                MyApp.mInstance?.appConfig = adSettings
                                MyApp.mInstance?.ctrBTNColor = adSettings.ctrColor
                                _appConfig.value = adSettings

                                continuation.resume(Result.success(adSettings))
                            }

                            else -> {
                                //  Log.d(TAG, "fetchRemoteConfig: exception ==== ${task.exception?.message}   =====\n \n \n result =====  ${task.result}   ")

                                continuation.resume(Result.failure(kotlin.Exception("Fetch failed  === ")))
                            }
                            // ... error handling
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "fetchRemoteConfig: catch  exception ==== ")

                    continuation.resume(Result.failure(e))
                }
            }
        }
    override var adSettingsSynced: Boolean = false
}
