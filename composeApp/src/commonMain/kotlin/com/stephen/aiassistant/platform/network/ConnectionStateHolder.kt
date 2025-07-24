package com.stephen.aiassistant.platform.network

import kotlinx.coroutines.flow.MutableStateFlow

interface ConnectionStateHolder {
    val isNetworkConnected: MutableStateFlow<NetworkUiState>
    fun start()
    fun stop()
}

/**
 * @param isFreeToUse 免费网络
 * @param isExpensive 付费网络
 * @param isConstrained 限制网络
 * @param isDisconnected 无网络
 */
data class NetworkUiState(
    val isFreeToUse: Boolean = true,
    val isExpensive: Boolean = false,
    val isConstrained: Boolean = false,
    val isDisconnected: Boolean = false
)