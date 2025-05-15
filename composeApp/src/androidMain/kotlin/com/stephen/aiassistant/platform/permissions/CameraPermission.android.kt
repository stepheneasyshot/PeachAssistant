package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.PermissionDelegate

import android.Manifest
import android.content.Context

actual val cameraDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null
    override fun getPlatformPermission() = listOf(Manifest.permission.CAMERA)
}