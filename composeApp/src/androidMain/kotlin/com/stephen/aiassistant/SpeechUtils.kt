package com.stephen.aiassistant

import android.speech.tts.TextToSpeech
import io.github.aakira.napier.Napier
import java.util.Locale

object SpeechUtils {

    private lateinit var textToSpeech: TextToSpeech

    private const val TEST_IDENTIFIER = "test"

    private const val TEST_HELLO = "Hi, this is a test."

    private var isConnected = false

    private val ttsConnectedListener = TextToSpeech.OnInitListener { status ->
        Napier.d("OnInitListener status: $status")
        isConnected = status == TextToSpeech.SUCCESS
    }

    fun init() {
        textToSpeech = TextToSpeech(appContext, ttsConnectedListener)
    }

    fun speak(text: String = TEST_HELLO, locale: Locale = Locale.US) {
        Napier.d("==========>speak<=========")
        if (isConnected) {
            textToSpeech.language = locale
            textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_ADD,
                null,
                TEST_IDENTIFIER
            )
        } else {
            Napier.d("==========>TTS is not connected!<=========")
        }
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun shutdown() {
        textToSpeech.shutdown()
    }
}
