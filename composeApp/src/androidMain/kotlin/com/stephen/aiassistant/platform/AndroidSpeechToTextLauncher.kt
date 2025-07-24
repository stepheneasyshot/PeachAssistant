package com.stephen.aiassistant.platform

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import java.util.UUID

class AndroidSpeechToTextLauncher : SpeechToTextLauncher {

    private val activityHolder = MutableStateFlow<Activity?>(null)

    private val launcherHolder = MutableStateFlow<ActivityResultLauncher<Intent>?>(null)

    private val key = UUID.randomUUID().toString()

    private var recognizeResult = ""

    private var recognizeResultCallback: RecognizeResultCallback? = null

    override fun launch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        launcherHolder.value?.launch(intent)
    }

    override fun waitForResult(onResult: (String) -> Unit) {
        recognizeResultCallback = RecognizeResultCallback(onResult)
    }

    fun bind(activity: ComponentActivity) {
        this.activityHolder.value = activity
        val activityResultRegistryOwner = activity as ActivityResultRegistryOwner

        val launcher = activityResultRegistryOwner.activityResultRegistry.register(
            key,
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            recognizeResult =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                    ?: "Failed to recognize speech"

            recognizeResultCallback?.onResult?.let {
                Napier.d("RecognizeResult: $recognizeResult")
                it(recognizeResult)
            }
        }

        launcherHolder.value = launcher

        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    this@AndroidSpeechToTextLauncher.activityHolder.value = null
                    this@AndroidSpeechToTextLauncher.launcherHolder.value = null
                    source.lifecycle.removeObserver(this)
                }
            }
        }
        activity.lifecycle.addObserver(observer)
    }

    // 添加中断方法
    override fun stopRecognizing() {
        activityHolder.value?.finishActivity(key.hashCode())
    }
}

class RecognizeResultCallback(
    val onResult: (String) -> Unit
)