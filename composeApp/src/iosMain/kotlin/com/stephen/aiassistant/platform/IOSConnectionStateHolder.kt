package com.stephen.aiassistant.platform

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Network.nw_interface_type_wifi
import platform.Network.nw_path_get_status
import platform.Network.nw_path_is_constrained
import platform.Network.nw_path_is_expensive
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.Network.nw_path_uses_interface_type
import platform.darwin.DISPATCH_QUEUE_SERIAL_WITH_AUTORELEASE_POOL
import platform.darwin.dispatch_queue_create

class IOSConnectionStateHolder : ConnectionStateHolder {
    // 网络连接状态 默认是true有网络
    override val isNetworkConnected: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val monitor = nw_path_monitor_create()

    override fun start() {
        val queue = dispatch_queue_create(
            label = "com.stephen.aiassistant.network.monitor",
            attr = DISPATCH_QUEUE_SERIAL_WITH_AUTORELEASE_POOL,
        )
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = nw_path_get_status(path)
            when {
                status == nw_path_status_satisfied -> {
                    val isWifi = nw_path_uses_interface_type(path, nw_interface_type_wifi)
                    val isExpensive = nw_path_is_expensive(path)
                    val isConstrained = nw_path_is_constrained(path)
                    val isMetered = !isWifi && (isExpensive || isConstrained)
                    // 无网络状态，取反
                    Napier.i("isMetered $isMetered")
                    isNetworkConnected.value = !isMetered
                }

                else -> isNetworkConnected.value = false
            }
        }
        nw_path_monitor_set_queue(monitor, queue)
        nw_path_monitor_start(monitor)
    }

    override fun stop() {
        nw_path_monitor_cancel(monitor)
    }

    override fun getStatus(): Boolean = isNetworkConnected.value
}