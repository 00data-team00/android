package com.data.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp:Application() {
    companion object {
        lateinit var instance: MyApp
            private set
    }
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}