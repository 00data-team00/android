package com.data.app.data.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.data.app.util.security.CryptoUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoUtils: CryptoUtils
) {
    private val PREFS_NAME = "com.data.app.app_prefs" // 이름 변경 가능
    private val KEY_ACCESS_TOKEN = "access_token_v2" // 버전 관리 명시

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAccessToken(accessToken: String?) {
        val editor = sharedPreferences.edit()
        if (accessToken != null) {
            editor.putString(KEY_ACCESS_TOKEN, accessToken)
            Timber.d("Access token saved.")
        } else {
            editor.remove(KEY_ACCESS_TOKEN) // null이면 기존 값 삭제
            Timber.d("Null access token received, cleared saved one.")
        }
        editor.apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearAccessToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
        Timber.d("Access token cleared from preferences.")
    }
}
