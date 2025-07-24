package com.stephen.aiassistant.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.stephen.aiassistant.helper.DATASTORE_FILE_NAME
import com.stephen.aiassistant.helper.createDataStore
import com.stephen.aiassistant.platform.network.IOSConnectionStateHolder
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
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

actual fun getSystemCurrentMills(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

@Composable
actual fun rememberSpeechToTextLauncher(): SpeechToTextLauncher = remember {
    IOSSpeechToTextLauncher()
}

@Composable
actual fun BindActivitylauncher(launcher: SpeechToTextLauncher) {

}

@OptIn(ExperimentalForeignApi::class)
actual fun createDataStoreMultiplatform(): DataStore<Preferences> = createDataStore(
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$DATASTORE_FILE_NAME"
    }
)