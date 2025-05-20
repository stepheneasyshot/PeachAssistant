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

            /**
             * 在 Android 系统里，当设备建立网络连接后，系统会尝试访问特定
             * 的验证服务器来检查网络是否能正常访问公共互联网。
             * 如果验证成功，就会给这个网络连接标记 NET_CAPABILITY_VALIDATED 能力。
             *
             * 当网络连接具备 NET_CAPABILITY_NOT_RESTRICTED 能力时，意味着该网络连接
             * 没有受到限制，设备可以正常通过这个网络访问资源。
             * 反之，若网络连接不具备此能力，说明该网络连接受到了某种限制，可能无法正常访问网络资源。
             * 常见限制场景 - 企业网络：在企业网络环境中，管理员可能会限制某些应用或者特定类型的网络
             * 流量，此时网络连接可能就不具备 NET_CAPABILITY_NOT_RESTRICTED 能力。
             * 公共 Wi-Fi：部分公共 Wi-Fi 网络会进行认证或者流量限制，在完成认证之前或者达到流量
             * 上限后，网络连接可能会受到限制。
             */
            val isConnected =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isRestricted =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            val isValidated =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val isMetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            Napier.d(
                "Network status -> isConnected: $isConnected, isRestricted: $isRestricted, isValidated: $isValidated, isMetered: $isMetered",
                tag = TAG
            )
            isNetworkConnected.update {
                it.copy(
                    isFreeToUse = isConnected && isRestricted && isValidated && isMetered,
                    isConstrained = !isMetered,
                    isExpensive = !isMetered,
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