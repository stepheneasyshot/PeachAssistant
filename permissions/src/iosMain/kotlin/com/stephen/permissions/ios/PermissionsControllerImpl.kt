package com.stephen.permissions.ios

import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionState
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class PermissionsControllerImpl : PermissionsControllerProtocol {

    override suspend fun providePermission(permission: Permission) {
        return permission.delegate.providePermission()
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return permission.delegate.getPermissionState() == PermissionState.Granted
    }

    override suspend fun getPermissionState(permission: Permission): PermissionState {
        return permission.delegate.getPermissionState()
    }

    override fun openAppSettings() {
        val settingsUrl: NSURL = NSURL.URLWithString(UIApplicationOpenSettingsURLString)!!
        UIApplication.sharedApplication.openURL(settingsUrl, mapOf<Any?, Any>(), null)
    }

}
