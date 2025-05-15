package com.stephen.aiassistant

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.stephen.aiassistant.di.appModule
import com.stephen.aiassistant.ui.ContentView
import com.stephen.aiassistant.ui.theme.DarkColorScheme
import com.stephen.aiassistant.ui.theme.LightColorScheme
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    // coil & filekit support
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
            }
            .build()
    }

    KoinApplication(application = { modules(appModule) }) {
        MaterialTheme(if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme) {
            ContentView()
        }
    }
}