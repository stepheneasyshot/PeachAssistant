package com.stephen.copywritinggene

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
    }
}

val appContext = AndroidApplication.instance.applicationContext