package com.stephen.aiassistant

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.stephen.aiassistant.di.appModule
import com.stephen.aiassistant.ui.ContentView
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App(onThemeChange: (Int) -> Unit = {}) {
    // coil & filekit support
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
            }
            .build()
    }

    KoinApplication(application = { modules(appModule) }) {
        ContentView(onThemeChange = onThemeChange)
    }
}