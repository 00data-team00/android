package com.data.app.util.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import timber.log.Timber
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoUtils @Inject constructor() {
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val KEY_ALIAS_PREFIX = "com.data.app.authtoken.v1." // 앱 고유의 prefix 권장
    private val AES_MODE = "AES/GCM/NoPadding"
    private val GCM_TAG_LENGTH = 128 // bits

    // 특정 데이터 유형에 대한 고유한 키 별칭 생성
    fun getAliasForRefreshToken(): String = KEY_ALIAS_PREFIX + "refresh_token"

    @Throws(Exception::class)
    private fun getOrCreateSecretKey(alias: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (!keyStore.containsAlias(alias)) {
            Timber.d("Keystore: Generating new secret key for alias: $alias")
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true) // GCM은 IV를 사용하므로 true가 적합
            // .setUserAuthenticationRequired(false) // 키 사용에 생체 인증 불필요 (BiometricPrompt가 앱 레벨에서 담당)
            keyGenerator.init(builder.build())
            return keyGenerator.generateKey()
        }
        Timber.d("Keystore: Using existing secret key for alias: $alias")
        return keyStore.getKey(alias, null) as SecretKey
    }

    /**
     * 데이터를 암호화하고 (암호문, IV) 쌍을 Base64 문자열로 반환합니다.
     */
    @Throws(Exception::class)
    fun encryptData(alias: String, dataToEncrypt: String): Pair<String, String>? {
        if (dataToEncrypt.isEmpty()) {
            Timber.w("Data to encrypt is empty for alias $alias")
            return null
        }
        try {
            val secretKey = getOrCreateSecretKey(alias)
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv // 생성된 IV 저장 필요
            val encryptedByteArray = cipher.doFinal(dataToEncrypt.toByteArray(Charset.forName("UTF-8"))) // 변수명 변경

            return Pair(
                Base64.encodeToString(encryptedByteArray, Base64.NO_WRAP), // <<< 수정됨
                Base64.encodeToString(iv, Base64.NO_WRAP)                  // <<< 수정됨
            )
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed for alias: $alias")
            throw e // 또는 null 반환 후 호출부에서 처리
        }
    }

    /**
     * Base64로 인코딩된 (암호문, IV)를 받아 원본 데이터를 복호화합니다.
     */
    @Throws(Exception::class)
    fun decryptData(alias: String, encryptedDataString: String?, ivString: String?): String? {
        if (encryptedDataString.isNullOrEmpty() || ivString.isNullOrEmpty()) {
            Timber.w("Encrypted data or IV is null/empty for alias $alias. Cannot decrypt.")
            return null
        }
        try {
            val secretKey = getOrCreateSecretKey(alias) // 키가 없으면 생성 시도 (일관성 유지)
            val cipher = Cipher.getInstance(AES_MODE)

            val encryptedDataBytes = Base64.decode(encryptedDataString, Base64.NO_WRAP) // <<< 수정됨, 변수명 변경
            val ivBytes = Base64.decode(ivString, Base64.NO_WRAP)                       // <<< 수정됨, 변수명 변경
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, ivBytes) // byte array 사용

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(encryptedDataBytes)
            return String(decryptedBytes, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed for alias: $alias")
            // 키가 변경되었거나 데이터가 손상된 경우 복호화 실패 가능
            // 이 경우, 저장된 토큰을 삭제하고 사용자가 다시 로그인하도록 유도해야 할 수 있음
            throw e // 또는 null 반환 후 호출부에서 처리
        }
    }

    /**
     * 특정 별칭의 키를 Keystore에서 삭제합니다. (예: 로그아웃 시)
     */
    fun deleteKey(alias: String) {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
                Timber.d("Keystore: Deleted key for alias: $alias")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete key for alias: $alias")
        }
    }
}