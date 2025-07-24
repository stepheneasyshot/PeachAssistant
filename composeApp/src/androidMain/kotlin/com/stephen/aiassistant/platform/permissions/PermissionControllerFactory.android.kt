package com.stephen.aiassistant.platform.permissions

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.stephen.permissions.PermissionsController
import com.stephen.permissions.PermissionsControllerImpl

@Composable
actual fun rememberPermissionsControllerFactory(): PermissionsControllerFactory {
    val context = LocalContext.current
    return remember(context) {
        PermissionsControllerFactory {
            PermissionsControllerImpl(context as ComponentActivity)
        }
    }
}

@Composable
actual fun BindEffect(permissionsController: PermissionsController) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        // 绑定到activity
        permissionsController.bind(context as ComponentActivity)
    }
}