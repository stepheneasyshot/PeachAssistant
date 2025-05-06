package com.stephen.simpleweather.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun rememberStatusBarHeight(): Dp {
    val density = LocalDensity.current
    val statusBars = WindowInsets.statusBars

    return remember {
        with(density) {
            statusBars.getTop(this).toDp()
        }
    }
}
