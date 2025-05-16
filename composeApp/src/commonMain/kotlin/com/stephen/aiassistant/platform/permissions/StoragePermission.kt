package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionDelegate

internal expect val storageDelegate: PermissionDelegate
internal expect val writeStorageDelegate: PermissionDelegate

object StoragePermission : Permission {
    override val delegate get() = storageDelegate
}

object WriteStoragePermission : Permission {
    override val delegate get() = writeStorageDelegate
}

val Permission.Companion.STORAGE get() = StoragePermission
val Permission.Companion.WRITE_STORAGE get() = WriteStoragePermission