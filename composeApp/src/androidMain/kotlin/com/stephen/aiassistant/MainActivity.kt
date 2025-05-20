package com.stephen.aiassistant

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.stephen.aiassistant.data.ThemeState
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.manualFileKitCoreInitialization

class MainActivity : ComponentActivity() {

    private var internalThemeState = ThemeState.SYSTEM

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileKit.manualFileKitCoreInitialization(this)
        enableEdgeToEdge()
        setContent {
            App { theme ->
                internalThemeState = theme
                updateSystemBarAppearance()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SpeechUtils.shutdown()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateSystemBarAppearance()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateSystemBarAppearance() {
        val isNightModeActive =
            internalThemeState == ThemeState.DARK
                    || (internalThemeState == ThemeState.SYSTEM && resources.configuration.isNightModeActive)
        window.insetsController?.apply {
            setSystemBarsAppearance(
                if (isNightModeActive) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }
}