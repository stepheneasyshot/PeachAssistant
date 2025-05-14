package com.stephen.aiassistant.platform.permissions

import android.Manifest
import android.content.Context
import com.stephen.permissions.PermissionDelegate

actual val recordAudioDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null
    override fun getPlatformPermission() = listOf(Manifest.permission.RECORD_AUDIO)
}
