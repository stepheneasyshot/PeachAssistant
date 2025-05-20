package com.stephen.aiassistant.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.stephen.aiassistant.SpeechUtils
import com.stephen.aiassistant.appContext
import com.stephen.aiassistant.helper.DATASTORE_FILE_NAME
import com.stephen.aiassistant.helper.createDataStore
import com.stephen.aiassistant.platform.network.AndroidConnectionStateHolder
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun copyTextToClipBoard(text: String) {
    // Get the ClipboardManager instance
    val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    // Create a ClipData object
    val clip = ClipData.newPlainText("Copied Text", text)
    // Set the ClipData to the clipboard
    clipboard.setPrimaryClip(clip)
}

actual fun getHttpEngine(): HttpClientEngineFactory<*> {
    return CIO
}

actual fun textToSpeech(text: String) {
    SpeechUtils.speak(text)
}

actual fun stopSpeaking() {
    SpeechUtils.stop()
}

actual fun getSystemCurrentMills(): Long = System.currentTimeMillis()

@Composable
actual fun rememberSpeechToTextLauncher(): SpeechToTextLauncher = remember {
    AndroidSpeechToTextLauncher()
}

@Composable
actual fun BindActivitylauncher(launcher: SpeechToTextLauncher) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        // 绑定到activity
        (launcher as AndroidSpeechToTextLauncher).bind(context as ComponentActivity)
    }
}

actual fun createDataStoreMultiplatform(): DataStore<Preferences> = createDataStore(
    producePath = { appContext.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath }
)