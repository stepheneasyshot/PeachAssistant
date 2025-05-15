package com.stephen.aiassistant.platform

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice
import platform.UIKit.UIPasteboard


class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun copyTextToClipBoard(text: String) {
    val pasteBoard = UIPasteboard.generalPasteboard
    pasteBoard.string = text
}

actual fun getHttpEngine(): HttpClientEngineFactory<*> {
    return Darwin
}

// Implementation #1 - uses `TextToSpeechManager` natively in Kotlin
var textToSpeechManager: TextToSpeechManager = TextToSpeechManager()
actual fun textToSpeech(text: String) {  // gives runtime error: [cataNapier] Unable to list voice folder
    textToSpeechManager.speak(text)
}

actual fun stopSpeaking() {
    textToSpeechManager.stopSpeaking()
}