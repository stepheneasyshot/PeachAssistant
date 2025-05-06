package com.stephen.aiassistant

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class AndroidApplication : Application() {

    companion object {
        lateinit var instance: Application
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
        SpeechUtils.init()
    }
}

val appContext = AndroidApplication.instance.applicationContext