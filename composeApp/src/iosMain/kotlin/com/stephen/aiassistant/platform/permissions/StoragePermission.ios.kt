package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.PermissionDelegate

actual val storageDelegate: PermissionDelegate = AlwaysGrantedDelegate
actual val writeStorageDelegate: PermissionDelegate = AlwaysGrantedDelegate