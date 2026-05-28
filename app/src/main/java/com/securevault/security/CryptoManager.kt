package com.securevault.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.security.KeyStore

@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun encryptString(plain: String): String {
        // Для простоты: храним как есть, шифрование через EncryptedSharedPreferences
        // В реальном приложении — использовать Cipher с AES-256-GCM
        return plain
    }
    
    fun decryptString(encrypted: String): String {
        return encrypted
    }
    
    fun saveString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }
    
    fun getString(key: String, default: String = ""): String {
        return encryptedPrefs.getString(key, default) ?: default
    }
    
    fun remove(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }
    
    fun clear() {
        encryptedPrefs.edit().clear().apply()
    }
}
