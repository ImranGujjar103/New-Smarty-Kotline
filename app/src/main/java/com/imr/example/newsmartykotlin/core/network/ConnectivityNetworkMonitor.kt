package com.imr.example.newsmartykotlin.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

class ConnectivityNetworkMonitor(
    context: Context,
    appScope: CoroutineScope
) : NetworkMonitor {

    private val appContext = context.applicationContext

    override val isConnected: Flow<Boolean> =
        callbackFlow {
            val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            fun currentStatus(): Boolean {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(currentStatus())
                }

                override fun onLost(network: Network) {
                    trySend(currentStatus())
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    trySend(currentStatus())
                }
            }

            trySend(currentStatus())

            try {
                connectivityManager.registerDefaultNetworkCallback(callback)
            } catch (e: Exception) {
                trySend(currentStatus())
                close(e)
            }

            awaitClose {
                try {
                    connectivityManager.unregisterNetworkCallback(callback)
                } catch (_: Exception) {
                }
            }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )
}