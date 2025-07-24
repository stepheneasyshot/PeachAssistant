package com.stephen.aiassistant.platform.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.stephen.permissions.PermissionsController
import com.stephen.permissions.ios.PermissionsControllerImpl

@Composable
actual fun rememberPermissionsControllerFactory(): PermissionsControllerFactory {
    return remember {
        PermissionsControllerFactory {
            PermissionsControllerImpl()
        }
    }
}

@Composable
actual fun BindEffect(permissionsController: PermissionsController) {

}
