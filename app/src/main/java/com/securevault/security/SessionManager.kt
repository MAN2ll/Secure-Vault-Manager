package com.securevault.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хранит хеш мастер-пароля в EncryptedSharedPreferences (AES256-backed).
 * Управляет состоянием разблокировки в памяти — не на диске.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "secure_vault_prefs"
        private const val KEY_MASTER_HASH = "master_password_hash"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_SETUP_DONE = "setup_done"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Состояние сессии только в памяти — при закрытии приложения сбрасывается
    @Volatile
    private var isUnlocked: Boolean = false

    fun isSetupDone(): Boolean = prefs.getBoolean(KEY_SETUP_DONE, false)

    fun getMasterHash(): String? = prefs.getString(KEY_MASTER_HASH, null)

    fun saveMasterHash(hash: String) {
        prefs.edit()
            .putString(KEY_MASTER_HASH, hash)
            .putBoolean(KEY_SETUP_DONE, true)
            .apply()
    }

    fun unlock() { isUnlocked = true }
    fun lock() { isUnlocked = false }
    fun isUnlocked(): Boolean = isUnlocked

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
        isUnlocked = false
    }
}
