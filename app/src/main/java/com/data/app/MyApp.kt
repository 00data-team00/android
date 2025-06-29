package com.data.app

import android.app.Application
import android.content.Context
import com.data.app.util.security.updateLocale
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

    override fun attachBaseContext(base: Context) {
        val prefs = base.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "ko") ?: "ko"
        val newContext = base.updateLocale(lang)
        super.attachBaseContext(newContext)
    }
}