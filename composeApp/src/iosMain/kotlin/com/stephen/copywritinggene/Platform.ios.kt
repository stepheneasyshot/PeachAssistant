package com.stephen.copywritinggene

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