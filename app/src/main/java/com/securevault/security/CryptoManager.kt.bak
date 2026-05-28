package com.securevault.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер шифрования AES-256-GCM с использованием Android Keystore.
 * Ключи хранятся в аппаратном защищённом хранилище и никогда не покидают Keystore.
 */
@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "SecureVaultMasterKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128 // bits
        private const val IV_LENGTH = 12 // bytes (96 bits — рекомендован для GCM)
        private const val KEY_SIZE = 256 // AES-256
    }

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    /**
     * Генерирует или возвращает существующий AES-256 ключ из Android Keystore.
     * KeyStore хранит ключ в аппаратном Security Element / StrongBox (если доступен).
     */
    private fun getOrCreateKey(): SecretKey {
        keyStore.getEntry(KEY_ALIAS, null)?.let { entry ->
            return (entry as KeyStore.SecretKeyEntry).secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(KEY_SIZE)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false) // управляется на уровне приложения
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Шифрует данные с AES-256-GCM.
     * Возвращает [IV (12 байт)] + [зашифрованные данные + GCM тег (16 байт)]
     */
    fun encrypt(plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())

        val iv = cipher.iv // 12 байт IV генерируется автоматически
        val ciphertext = cipher.doFinal(plaintext)

        // Упаковываем: IV (12) + ciphertext+tag
        return iv + ciphertext
    }

    /**
     * Расшифровывает данные зашифрованные encrypt().
     * Формат входа: [IV (12 байт)] + [ciphertext + GCM тег]
     */
    fun decrypt(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, IV_LENGTH)
        val ciphertext = encryptedData.copyOfRange(IV_LENGTH, encryptedData.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)

        return cipher.doFinal(ciphertext)
    }

    /**
     * Шифрует строку и возвращает Base64.
     */
    fun encryptString(plaintext: String): String {
        val encrypted = encrypt(plaintext.toByteArray(Charsets.UTF_8))
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
    }

    /**
     * Расшифровывает Base64-строку.
     */
    fun decryptString(encryptedBase64: String): String {
        val encrypted = android.util.Base64.decode(encryptedBase64, android.util.Base64.NO_WRAP)
        return decrypt(encrypted).toString(Charsets.UTF_8)
    }

    /**
     * Удаляет ключ из Keystore (при сбросе приложения).
     */
    fun deleteKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.deleteEntry(KEY_ALIAS)
        }
    }
}
