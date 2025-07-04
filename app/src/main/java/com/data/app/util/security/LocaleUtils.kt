package com.data.app.util.security

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

fun Context.updateLocale(languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(resources.configuration)
    config.setLocale(locale)

    return createConfigurationContext(config)
}

fun Context.resetToSystemLocale(): Context {
    val defaultLocale = Resources.getSystem().configuration.locales.get(0)
    Locale.setDefault(defaultLocale)

    val config = Configuration(resources.configuration)
    config.setLocale(defaultLocale)

    return createConfigurationContext(config)
}