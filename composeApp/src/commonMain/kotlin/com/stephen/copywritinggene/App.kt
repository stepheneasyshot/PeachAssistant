package com.stephen.copywritinggene

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.stephen.copywritinggene.di.appModule
import com.stephen.copywritinggene.ui.ContentView
import com.stephen.copywritinggene.ui.theme.DarkColorScheme
import com.stephen.copywritinggene.ui.theme.LightColorScheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        MaterialTheme(if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme) {
            ContentView()
        }
    }
}