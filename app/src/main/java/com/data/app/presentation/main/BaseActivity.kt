package com.data.app.presentation.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.data.app.util.security.updateLocale

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "ko") ?: "ko"
        val context = newBase.updateLocale(lang)
        super.attachBaseContext(context)
    }
}
