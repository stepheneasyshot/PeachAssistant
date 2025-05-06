package com.stephen.aiassistant

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
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
