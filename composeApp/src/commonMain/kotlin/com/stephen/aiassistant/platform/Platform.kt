package com.stephen.aiassistant.platform

import io.ktor.client.engine.HttpClientEngineFactory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun copyTextToClipBoard(text: String)

expect fun getHttpEngine(): HttpClientEngineFactory<*>

expect fun textToSpeech(text: String)

expect fun stopSpeaking()

