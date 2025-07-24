package com.stephen.aiassistant.platform.permissions

import androidx.compose.runtime.Composable
import com.stephen.permissions.PermissionsController


fun interface PermissionsControllerFactory {
    fun createPermissionsController(): PermissionsController
}

@Composable
expect fun rememberPermissionsControllerFactory(): PermissionsControllerFactory

@Composable
expect fun BindEffect(permissionsController: PermissionsController)
