package com.stephen.aiassistant.platform.network

actual fun  getPlatformConnectionManager(): ConnectionStateHolder = IOSConnectionStateHolder()
