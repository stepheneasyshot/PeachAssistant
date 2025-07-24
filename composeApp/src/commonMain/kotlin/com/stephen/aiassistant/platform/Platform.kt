package com.stephen.aiassistant.platform

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.stephen.aiassistant.platform.network.ConnectionStateHolder
import io.ktor.client.engine.HttpClientEngineFactory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun copyTextToClipBoard(text: String)

expect fun getHttpEngine(): HttpClientEngineFactory<*>

expect fun textToSpeech(text: String)

expect fun stopSpeaking()

expect fun getSystemCurrentMills(): Long

interface SpeechToTextLauncher {
    fun launch()
    fun waitForResult(onResult: (String) -> Unit)
    fun stopRecognizing()
}

@Composable
expect fun rememberSpeechToTextLauncher(): SpeechToTextLauncher

@Composable
expect fun BindActivitylauncher(launcher: SpeechToTextLauncher)

expect fun createDataStoreMultiplatform(): DataStore<Preferences>
