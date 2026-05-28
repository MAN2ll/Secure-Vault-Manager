package com.securevault.security

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val crypto: CryptoManager
) {
    companion object {
        private const val KEY_MASTER_HASH = "master_hash"
        private const val KEY_BIOMETRIC = "biometric_enabled"
        private const val KEY_UNLOCKED = "is_unlocked"
    }
    
    val isSetupDone: Boolean
        get() = crypto.getString(KEY_MASTER_HASH).isNotEmpty()
    
    var isBiometricEnabled: Boolean
        get() = crypto.getString(KEY_BIOMETRIC, "false") == "true"
        set(value) = crypto.saveString(KEY_BIOMETRIC, value.toString())
    
    fun saveMasterHash(hash: String) {
        crypto.saveString(KEY_MASTER_HASH, hash)
    }
    
    fun getMasterHash(): String {
        return crypto.getString(KEY_MASTER_HASH)
    }
    
    fun unlock() {
        crypto.saveString(KEY_UNLOCKED, "true")
    }
    
    fun lock() {
        crypto.saveString(KEY_UNLOCKED, "false")
    }
    
    val isUnlocked: Boolean
        get() = crypto.getString(KEY_UNLOCKED, "false") == "true"
}
