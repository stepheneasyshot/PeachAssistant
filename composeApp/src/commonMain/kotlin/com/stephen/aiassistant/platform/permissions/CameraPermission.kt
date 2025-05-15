package com.stephen.aiassistant.platform.permissions

import com.stephen.permissions.Permission
import com.stephen.permissions.PermissionDelegate

internal expect val cameraDelegate: PermissionDelegate

object CameraPermission : Permission {
    override val delegate get() = cameraDelegate
}

val Permission.Companion.CAMERA get() = CameraPermission