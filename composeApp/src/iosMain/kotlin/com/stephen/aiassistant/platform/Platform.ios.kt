package com.stephen.aiassistant.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.stephen.aiassistant.helper.DATASTORE_FILE_NAME
import com.stephen.aiassistant.helper.createDataStore
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSValue
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.CGRectValue
import platform.UIKit.UIDevice
import platform.UIKit.UIKeyboardFrameEndUserInfoKey
import platform.UIKit.UIKeyboardWillChangeFrameNotification
import platform.UIKit.UIPasteboard
import platform.UIKit.UIScreen
import platform.darwin.NSObject
import platform.darwin.sel_registerName

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

actual class KeyboardHeightProvider {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    @Composable
    actual fun getKeyboardHeightAsState(): State<Float> {
        val keyboardHeight = remember { mutableStateOf(0f) }

        DisposableEffect(Unit) {
            val observer = object : NSObject() {
                @ObjCAction
                fun keyboardWillChangeFrame(notification: NSNotification) {
                    val userInfo = notification.userInfo
                    if (userInfo != null) {
                        val keyboardFrameValue = userInfo[UIKeyboardFrameEndUserInfoKey] as NSValue
                        val keyboardFrame = keyboardFrameValue.CGRectValue()
                        val screenHeight = UIScreen.mainScreen.bounds.useContents { size.height }

                        keyboardFrame.useContents { // 使用 useContents 块来访问 CGRect 的成员
                            val currentKeyboardOriginY = origin.y // 现在可以直接访问 origin.y

                            // 计算键盘的高度
                            val currentKeyboardHeight = if (currentKeyboardOriginY < screenHeight) {
                                (screenHeight - currentKeyboardOriginY).toFloat()
                            } else {
                                0f
                            }
                            keyboardHeight.value = currentKeyboardHeight
                        }
                    }
                }
            }

            // 添加键盘通知监听器
            NSNotificationCenter.defaultCenter.addObserver(
                observer = observer,
                selector = sel_registerName("keyboardWillChangeFrame:"),
                name = UIKeyboardWillChangeFrameNotification,
                `object` = null
            )

            onDispose {
                // 移除键盘通知监听器
                NSNotificationCenter.defaultCenter.removeObserver(
                    observer,
                    UIKeyboardWillChangeFrameNotification,
                    null
                )
            }
        }

        return keyboardHeight
    }
}