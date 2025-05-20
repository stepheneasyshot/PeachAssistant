package com.stephen.aiassistant.platform.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.stephen.aiassistant.appContext
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AndroidConnectionStateHolder : ConnectionStateHolder {

    companion object {
        private const val TAG = "AndroidConnectionStateHolder"
    }

    override val isNetworkConnected: MutableStateFlow<NetworkUiState> =
        MutableStateFlow(NetworkUiState())

    private var connectivityManager: ConnectivityManager? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Napier.d("Network available", tag = TAG)
            isNetworkConnected.update {
                it.copy(isDisconnected = false)
            }
        }

        override fun onLost(network: Network) {
            Napier.d("Network lost", tag = TAG)
            isNetworkConnected.update {
                it.copy(isDisconnected = true)
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)

            val isConnected =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isNotRestricted =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            val isValidated =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val isNotMetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            Napier.d(
                "Network status -> isConnected: $isConnected, isNotRestricted: $isNotRestricted, isValidated: $isValidated, isNotMetered: $isNotMetered ",
                tag = TAG
            )
            isNetworkConnected.update {
                it.copy(
                    isFreeToUse = isConnected && isNotRestricted && isValidated && isNotMetered,
                    isConstrained = !isNotRestricted,
                    isExpensive = !isNotMetered,
                    isDisconnected = !isConnected
                )
            }
        }
    }

    override fun start() {
        try {
            if (connectivityManager == null) {
                connectivityManager =
                    appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            }
            // API 24 and above
            connectivityManager?.registerDefaultNetworkCallback(networkCallback)

            val currentNetwork = connectivityManager?.activeNetwork

            if (currentNetwork == null) {
                isNetworkConnected.update {
                    it.copy(isDisconnected = true)
                }
                Napier.d("Disconnected", tag = TAG)
            }

            Napier.d("Started", tag = TAG)
        } catch (e: Exception) {
            Napier.d("Failed to start: ${e.message.toString()}", tag = TAG)
            e.printStackTrace()
            isNetworkConnected.update {
                it.copy(isDisconnected = true)
            }
        }
    }

    override fun stop() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        Napier.d("Stopped", tag = TAG)
    }
}