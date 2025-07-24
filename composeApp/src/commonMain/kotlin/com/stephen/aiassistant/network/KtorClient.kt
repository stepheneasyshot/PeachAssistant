package com.stephen.aiassistant.network

import com.stephen.aiassistant.platform.getHttpEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class KtorClient {

    val client = HttpClient(getHttpEngine()) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000L
            connectTimeoutMillis = 10_000L
            socketTimeoutMillis = 30_000L
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(WebSockets) {
            pingInterval = Duration.parse("15s")
        }
    }

    fun release() {
        client.close()
    }
}
