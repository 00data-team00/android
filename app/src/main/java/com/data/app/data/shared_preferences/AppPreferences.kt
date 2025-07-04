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
        val alias = cryptoUtils.getAliasForRefreshToken()


        if (accessToken != null) {
            try {
                val (encryptedToken, iv) = cryptoUtils.encryptData(alias, accessToken) ?: return
                editor.putString(KEY_ACCESS_TOKEN, encryptedToken)
                editor.putString("${KEY_ACCESS_TOKEN}_iv", iv)
                Timber.d("Access token encrypted and saved.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to encrypt access token.")
            }
        } else {
            editor.remove(KEY_ACCESS_TOKEN)
            editor.remove("${KEY_ACCESS_TOKEN}_iv")
            Timber.d("Null access token received, cleared saved one.")
        }

        editor.apply()
    }

    fun getAccessToken(): String? {
        val alias = cryptoUtils.getAliasForRefreshToken()
        val encryptedToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        val iv = sharedPreferences.getString("${KEY_ACCESS_TOKEN}_iv", null)

        return try {
            cryptoUtils.decryptData(alias, encryptedToken, iv)
        } catch (e: Exception) {
            Timber.e(e, "Failed to decrypt access token.")
            null
        }
    }

    fun clearAccessToken() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove("${KEY_ACCESS_TOKEN}_iv")
            .apply()

        // KeyStore에서도 제거
        try {
            cryptoUtils.deleteKey(cryptoUtils.getAliasForRefreshToken())
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete encryption key during logout.")
        }

        Timber.d("Access token cleared from preferences and keystore.")
    }
}
