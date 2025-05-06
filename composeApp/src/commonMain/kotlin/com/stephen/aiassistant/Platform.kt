package com.stephen.aiassistant

import io.ktor.client.engine.HttpClientEngineFactory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun copyTextToClipBoard(text: String)

expect fun getHttpEngine(): HttpClientEngineFactory<*>