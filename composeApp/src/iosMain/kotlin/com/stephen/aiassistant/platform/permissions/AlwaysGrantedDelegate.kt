package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.PermissionDelegate
import com.stephen.permissions.PermissionState

object AlwaysGrantedDelegate : PermissionDelegate {
    override suspend fun providePermission() = Unit
    override suspend fun getPermissionState() = PermissionState.Granted
}