package com.stephen.aiassistant.platform.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import com.stephen.permissions.PermissionDelegate

actual val storageDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null

    override fun getPlatformPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
}

actual val writeStorageDelegate = object : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context) = null

    override fun getPlatformPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
}