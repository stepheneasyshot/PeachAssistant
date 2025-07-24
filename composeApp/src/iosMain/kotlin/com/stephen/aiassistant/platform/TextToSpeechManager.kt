package com.stephen.aiassistant.platform

import io.github.aakira.napier.Napier
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesisVoiceQualityEnhanced
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechSynthesizerDelegateProtocol
import platform.AVFAudio.AVSpeechUtterance
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.NaturalLanguage.NLLanguageRecognizer
import platform.darwin.NSObject

// Implementation #1
// Kotlin-native Implementation of iOS Text-to-Speech
// Developers Note: must create an instance at runtime, cant use object here (!) https://github.com/JetBrains/kotlin-native/issues/3855
class TextToSpeechManager : NSObject(), AVSpeechSynthesizerDelegateProtocol {
    private var synthesizer: AVSpeechSynthesizer = AVSpeechSynthesizer()
    var isSpeaking = false
    var isPaused = false
    var utterance = AVSpeechUtterance()

    init {
        synthesizer.delegate = this
    }

    fun speak(text: String) {
        // Resume speaking if there is unspoken text
        if (!isSpeaking && isPaused) {
            continueSpeaking()
        }

        // Find all english voices
        val englishVoices = AVSpeechSynthesisVoice.speechVoices()
            .filter {
                it as AVSpeechSynthesisVoice
                it.language.startsWith("en")
            }
        for (voice in englishVoices) {
            voice as AVSpeechSynthesisVoice
            val quality =
                if (voice.quality == AVSpeechSynthesisVoiceQualityEnhanced) "Enhanced" else "Default"
            Napier.d("${voice.name}: $quality [${voice.language}], id: ${voice.identifier}, qual: ${voice.quality}")
        }

        Napier.d("Current Locale: ${NSLocale.currentLocale().languageCode}")
        Napier.d("Current Locale voice: ${AVSpeechSynthesisVoice.voiceWithLanguage(NSLocale.currentLocale().languageCode)}")

        // Recognize dominant language of text
        utterance = AVSpeechUtterance.speechUtteranceWithString(text)
        val recognizer = NLLanguageRecognizer()
        recognizer.processString(text)
        val language = recognizer.dominantLanguage
        language?.run {
            // Default to recognized language
            utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage(language)

            // If utterance is english, attempt to find an Enhanced quality voice
            if (language == "en") {
                for (voice in englishVoices) {
                    voice as AVSpeechSynthesisVoice
                    if (voice.language.startsWith(NSLocale.currentLocale().languageCode)
                        && voice.quality == AVSpeechSynthesisVoiceQualityEnhanced
                    ) {
                        utterance.voice = voice // use the first enhanced one on the list
                        Napier.d("Enhanced Voice found: ${voice.name}")
                        break
                    }
                }
            }

            utterance.voice ?: run {
                Napier.d("Voice not found for language: ${utterance.voice?.language}")
                return
            }

            Napier.d("Voice found: ${utterance.voice?.name}")
            Napier.d("Voice quality: ${utterance.voice?.quality}")
            Napier.d("Voice language: ${utterance.voice?.language}")
        } ?: run {
            Napier.d("No dominant language found, defaulting to current locale: ${NSLocale.currentLocale().languageCode}")

            utterance.voice =
                AVSpeechSynthesisVoice.voiceWithLanguage(NSLocale.currentLocale().languageCode)
        }

        isPaused = false
        isSpeaking = true
        synthesizer.speakUtterance(utterance)
    }

    override fun speechSynthesizer(
        synthesizer: AVSpeechSynthesizer,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") //https://youtrack.jetbrains.com/issue/KT-43791/cocoapods-generated-code-with-same-parameter-types-and-order-but-different-names#focus=Comments-27-4574011.0-0
        didFinishSpeechUtterance: AVSpeechUtterance
    ) {
        isSpeaking = false
    }

    // Stop speaking
    fun stopSpeaking() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        isSpeaking = false
        isPaused = false
    }

    fun pauseSpeaking() {
        synthesizer.pauseSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        isPaused = true
        isSpeaking = false
    }

    fun continueSpeaking() {
        synthesizer.continueSpeaking()
        isPaused = false
        isSpeaking = true
    }

    fun isSpeaking(): Boolean {
        return isSpeaking
    }
}
